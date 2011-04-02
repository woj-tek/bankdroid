package bankdroid.tool.translation;

import java.io.File;

public class Main
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( final String[] args ) throws Exception
	{
		//FIXME read parameters from args
		final String projectLocation = "../bankdroid.start";

		final ProjectExplorer pe = new ProjectExplorer(projectLocation);

		final String[] languages = pe.getCurrentLanguages();

		final File defaultStrings = pe.getFile(Strings.FILE_TYPE, ProjectExplorer.DEFAULT_LANGUAGE);
		for ( final String lang : languages )
		{
			if ( lang.equals(ProjectExplorer.DEFAULT_LANGUAGE) )
				continue;
			try
			{
				final Strings other = new Strings(pe.getFile(Strings.FILE_TYPE, lang));
				other.writeToFile(defaultStrings);
			}
			catch ( final Exception e )
			{
				System.out.println("Skipping language " + lang + " due critical error.");
				e.printStackTrace();
			}
		}
	}
}
