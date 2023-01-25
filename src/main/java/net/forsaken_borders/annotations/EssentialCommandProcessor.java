package net.forsaken_borders.annotations;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("EssentialCommand")
public class EssentialCommandProcessor extends AbstractProcessor {

	// Is this the best way to tell our Processor which server to register to? It's certainly the easiest one
	// Does it work? Who knows, we need to add '-processor EssentialCommandProcessor' to every javac call
	// and I have no idea how to do that.
	// TODO: Actually use our Annotation Processor, or get rid of it
	public static MinecraftServer server;

	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();

		for (Element element : roundEnv.getElementsAnnotatedWith(EssentialCommand.class)) {

			// This is the last line of defence, in case someone manages to use our Annotation on something which is not
			// a Method, even tho it has the @Target(ElementType.METHOD) Annotation
			if (element.getKind() != ElementKind.METHOD) {
				continue;
			}

			ExecutableElement method = (ExecutableElement) element;
			EssentialCommand annotation = element.getAnnotation(EssentialCommand.class);
			LiteralArgumentBuilder<ServerCommandSource> commandNode = CommandManager.literal(annotation.name());

			// Since Annotations can only take Enums, we have to do this mess... or parse a string or smth even stupid-er
			// IntelliJ is great at not telling me every class which implements com.mojang.brigadier.arguments.ArgumentType,
			// so this list may not contain all available Argument Types
			// TODO: Find all Argument Types we can use
			for (EssentialArgument argument : annotation.arguments()) {
				switch (argument.type()) {
					case String -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), StringArgumentType.greedyString()));
					case Integer -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), IntegerArgumentType.integer()));
					case Float -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), FloatArgumentType.floatArg()));
					case Double -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), DoubleArgumentType.doubleArg()));
					case Long -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), LongArgumentType.longArg()));
					case Bool -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), BoolArgumentType.bool()));
					case Entity -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), EntityArgumentType.entity()));
					case Entities -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), EntityArgumentType.entities()));
					case BlockPosition -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), BlockPosArgumentType.blockPos()));
					case Player -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), EntityArgumentType.player()));
					case Players -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), EntityArgumentType.players()));
					case Color -> commandNode.then(RequiredArgumentBuilder.argument(argument.name(), ColorArgumentType.color()));
					default -> throw new IllegalStateException("Unexpected value: " + argument.type());
				}
			}

			commandNode.executes((context) -> {
				try {
					TypeElement classElement = (TypeElement) method.getEnclosingElement();
					String className = classElement.getQualifiedName().toString();
					Class<?> clazz = Class.forName(className);
					Object instance = clazz.getConstructor().newInstance();
					Method m = clazz.getMethod(method.getSimpleName().toString(), CommandContext.class);

					return (Integer) m.invoke(instance, context);
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				// I don't know what were supposed to return. In Spigot, you return true if the command succeeded, and false if it failed.
				// Do we return 1 and 0 instead, here? Or... does this int represent something else?
				// TODO: Figure this int return out
				return 0;
			});

			// Actually register the Command
			dispatcher.getRoot().addChild(commandNode.build());
		}
		return true;
	}
}
