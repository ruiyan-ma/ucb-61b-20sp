package gitlet;

import java.io.File;

/**
 * This class represents a file in .gitlet.
 *
 * @author ryan ma
 */

class GitletFile {

	/**
	 * Constructor function with FILE.
	 */
	GitletFile(File file) {
		_file = file;
	}

	/**
	 * Return String from file.
	 */
	String readFromFile() {
		return Utils.readContentsAsString(_file);
	}

	/**
	 * Write String CONTENT to file.
	 */
	void writeToFile(String content) {
		Utils.writeContents(_file, content);
	}

	/**
	 * The absolute path to this file.
	 */
	private final File _file;

}
