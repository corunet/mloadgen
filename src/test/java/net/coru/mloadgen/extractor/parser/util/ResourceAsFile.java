package net.coru.mloadgen.extractor.parser.util;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public final class ResourceAsFile {

	public String getContent(String fileName) throws Exception {
		return IOUtils.toString(
						getClass().getResourceAsStream(fileName),
						StandardCharsets.UTF_8
		);
	}

	public File getFile(String fileName) throws URISyntaxException {
		return new File(getClass().getResource(fileName).toURI());
	}
}
