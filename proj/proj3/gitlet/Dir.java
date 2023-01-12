package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is the abstract class for a directory.
 *
 * @author ryan ma
 */

abstract class Dir {

	/**
	 * Return the folder of this Dir.
	 */
	abstract File getFolder();

	/**
	 * Return a list of all files in this Dir.
	 */
	protected List<String> getAllFiles() {
		return Utils.plainFilenamesIn(getFolder());
	}

	/**
	 * Return true if the file FILENAME exists in this Dir.
	 */
	protected boolean checkExist(String fileName) {
		return getPathOfFile(fileName).exists();
	}

	/**
	 * Return the absolute path of file FILENAME.
	 */
	protected File getPathOfFile(String fileName) {
		return Utils.join(getFolder(), fileName);
	}

	/**
	 * Return String read from file FILENAME.
	 */
	protected String readFromFile(String fileName) {
		return Utils.readContentsAsString(getPathOfFile(fileName));
	}

	/**
	 * Write CONTENT to file FILENAME.
	 */
	protected void writeToFile(String fileName, String content) {
		Utils.writeContents(getPathOfFile(fileName), content);
	}

	/**
	 * Add file FILENAME to working directory.
	 */
	protected void addFile(String fileName) throws IOException {
		assert !getAllFiles().contains(fileName);
		getPathOfFile(fileName).createNewFile();
	}

	/**
	 * Delete the file FILENAME in working directory.
	 */
	protected void deleteFile(String fileName) {
		if (checkExist(fileName)) {
			Utils.restrictedDelete(getPathOfFile(fileName));
		}
	}

}
