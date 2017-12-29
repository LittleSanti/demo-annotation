package demo.processors;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;

public class OutputManager
{
	private static final OutputManager INSTANCE=new OutputManager();

	public static OutputManager getInstance()
	{
		return INSTANCE;
	}

	private OutputStream out;

	private OutputManager()
	{
	}

	public OutputStream getOutputStream(Filer filer, Location location, Element[] elements)
		throws IOException
	{
		if (this.out == null)
		{
			FileObject fo=filer.createResource(location, "pak", "enero", elements);
			this.out=fo.openOutputStream();
		}
		return this.out;
	}

	@Override
	protected void finalize()
					throws Throwable
	{
		super.finalize();
		this.out.close();
	}
}
