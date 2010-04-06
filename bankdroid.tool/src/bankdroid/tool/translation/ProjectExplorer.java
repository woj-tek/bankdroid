package bankdroid.tool.translation;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

public class ProjectExplorer
{
	public final static String DEFAULT_LANGUAGE = "DEFAULT";

	public final static String DIR_RESOURCES = "res";
	public final static String DIR_VALUES = "values";

	private final File projectLocation;
	private Map<String, File> languages;

	public ProjectExplorer( final String projectLocation )
	{
		this.projectLocation = new File(projectLocation);

		if ( !this.projectLocation.exists() )
			throw new IllegalArgumentException("Project location doesn't exists: " + projectLocation);

		if ( !this.projectLocation.isDirectory() )
			throw new IllegalArgumentException("Project location must be a directory.");
	}

	public String[] getCurrentLanguages()
	{
		if ( languages == null )
		{
			final File res = new File(projectLocation, DIR_RESOURCES);
			if ( !res.exists() || !res.isDirectory() )
				throw new IllegalStateException("Resource directory is missing or invalid: " + res.getAbsolutePath());

			languages = new HashMap<String, File>();
			final File[] resDirs = res.listFiles(new FileFilter()
			{

				@Override
				public boolean accept( final File file )
				{
					return file.isDirectory() && file.getName().startsWith(DIR_VALUES);
				}
			});
			for ( final File dir : resDirs )
			{
				String language = DEFAULT_LANGUAGE;

				final String dirName = dir.getName();

				final int pos = dirName.indexOf("-");
				if ( pos > 0 )
				{
					language = dirName.substring(pos + 1);
				}

				languages.put(language, dir);
			}
		}

		return languages.keySet().toArray(new String[languages.size()]);
	}

	public File getFile( final String type, final String language )
	{
		final File dir = languages.get(language);
		if ( dir == null )
			throw new IllegalArgumentException("Invalid language: " + language);
		final File result = new File(dir, type + ".xml");
		if ( result == null || !result.exists() || !result.isFile() )
			throw new IllegalArgumentException("Missing or invalid file type: " + type);

		return result;
	}
}
