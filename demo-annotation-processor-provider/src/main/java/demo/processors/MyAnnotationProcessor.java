package demo.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardLocation;

import demo.annotations.MyAnnotation;

@SupportedAnnotationTypes({ "demo.annotations.MyAnnotation" })
public class MyAnnotationProcessor extends AbstractProcessor
{
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MyAnnotationProcessor: note: ");
		processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "MyAnnotationProcessor: warning: ");
		processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "MyAnnotationProcessor: MANDATORY_WARNING: ");
		try
		{
			FileObject fo=processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "my", "init.txt", new Element[0]);
			try (OutputStream out=fo.openOutputStream())
			{
				out.write("Hola amigos".getBytes());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		if (super.processingEnv.getFiler() != null)
		{
			try
			{
				Location location=StandardLocation.SOURCE_OUTPUT;
				Element[] elements= {};
				OutputStream out=OutputManager.getInstance().getOutputStream(super.processingEnv.getFiler(), location, elements);
				for (TypeElement annotation : annotations)
				{
					processAnnotation(annotation, roundEnv, out);
					// Set<? extends Element> types=roundEnv.getElementsAnnotatedWith(annotation);
					// out.write((types + "\n").getBytes());
				}
				if (roundEnv.processingOver())
				{
					out.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	private void processAnnotation(TypeElement annotation, RoundEnvironment roundEnv, OutputStream out)
		throws IOException
	{
		Set<? extends Element> types=roundEnv.getElementsAnnotatedWith(annotation);
		for (Element type : types)
		{
			processType(type, roundEnv, out);
		}
	}

	private void processType(Element type, RoundEnvironment roundEnv, OutputStream out)
					throws IOException
	{
		MyAnnotation myAnnotation=type.getAnnotation(MyAnnotation.class);
		String line=type.getSimpleName() + ": name=" + myAnnotation.name() + ", address=" + myAnnotation.address() + "\n";
		out.write(line.getBytes());
	}
}
