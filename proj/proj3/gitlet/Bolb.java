package gitlet;

/**
 * This class stores the content of a file. The content of a file
 * at a particular time is called bolb. Bolb objects are stored
 * in .gitlet/objects directory.
 * A new bolb should only be created when we run the add command.
 *
 * @author ryan ma
 */

class Bolb extends GitletObject {

	/**
	 * Constructor function with FILENAME.
	 */
	Bolb(String fileName) {
		_content = Repo.getWorkDir().readFromFile(fileName);
	}

	/**
	 * Return the content of this bolb.
	 */
	String getContent() {
		return _content;
	}

	@Override
	String getUID() {
		return Utils.sha1(_content);
	}

	/**
	 * The contents of this file.
	 */
	private final String _content;
}
