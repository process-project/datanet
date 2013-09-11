package pl.cyfronet.datanet.web.client.codetemplates;

import java.util.List;

import com.google.inject.Inject;

public class CodeTemplateGenerator {
	public enum Language {
		Bash,
		Ruby,
		Python
	}
	
	private static final String PREFIX = "codeTemplate";
	private static final String CURL_FIELD_TEMPLATE = "-F {field}";
	private static final String RUBY_REGULAR_FIELD_TEMPLATE = ":{field} => '{value}', ";
	private static final String RUBY_FILE_FIELD_TEMPLATE = ":{field} => File.new('{value}', 'rb'), ";
	private static final String PYTHON_REGULAR_FIELD_TEMPLATE = "'{field}': '{value}', ";
	private static final String PYTHON_FILE_FIELD_TEMPLATE = "'{field}': open('{value}', 'rb'), ";
	
	private CodeTemplates codeTemplates;
	
	@Inject
	public CodeTemplateGenerator(CodeTemplates codeTemplates) {
		this.codeTemplates = codeTemplates;
	}
	
	public String getCodeTemplate(Language language, String repositoryUrl, String entityName, List<String> regularFields, List<String> fileFields) {
		String codeTemplate = codeTemplates.getString(PREFIX + language.name());
		codeTemplate = codeTemplate.replaceAll("\\{repository_url\\}", repositoryUrl);
		codeTemplate = codeTemplate.replaceAll("\\{entity_name\\}", entityName);
		codeTemplate = codeTemplate.replaceAll("\\{fields\\}", getFields(language, regularFields, fileFields));
		
		return codeTemplate; 
	}

	private String getFields(Language language, List<String> regularFields, List<String> fileFields) {
		switch (language) {
			case Bash:
				return getBashCurlFields(regularFields, fileFields);
			case Ruby:
				return getRubyFields(regularFields, fileFields);
			case Python:
				return getPythonFields(regularFields, fileFields);
		}
		
		return null;
	}

	private String getRubyFields(List<String> regularFields, List<String> fileFields) {
		StringBuilder builder = new StringBuilder();
		
		for (String regularField : regularFields) {
			builder.append(RUBY_REGULAR_FIELD_TEMPLATE.replace("{field}", regularField).replace("{value}", codeTemplates.fieldRegularValue()));
		}
		
		for (String fileField : fileFields) {
			builder.append(RUBY_FILE_FIELD_TEMPLATE.replace("{field}", fileField).replace("{value}", codeTemplates.fieldFileValue()));
		}
		
		String result = builder.toString();
		
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 2);
		}
		
		return result;
	}

	private String getPythonFields(List<String> regularFields, List<String> fileFields) {
		StringBuilder builder = new StringBuilder();
		
		if (regularFields.size() > 0) {
			builder.append("data = {");
			
			for (String regularField : regularFields) {
				builder.append(PYTHON_REGULAR_FIELD_TEMPLATE.replace("{field}", regularField).replace("{value}", codeTemplates.fieldRegularValue()));
			}
			
			builder.delete(builder.length() - 2, builder.length());
			builder.append("}");
		}
		
		if (regularFields.size() > 0 && fileFields.size() > 0) {
			builder.append(", ");
		}
		
		if (fileFields.size() > 0) {
			builder.append("files = {");
			
			for (String fileField : fileFields) {
				builder.append(PYTHON_FILE_FIELD_TEMPLATE.replace("{field}", fileField).replace("{value}", codeTemplates.fieldFileValue()));
			}
			
			builder.delete(builder.length() - 2, builder.length());
			builder.append("}");
		}
		
		return builder.toString();
	}

	private String getBashCurlFields(List<String> regularFields, List<String> fileFields) {
		StringBuilder builder = new StringBuilder();
		
		for (String regularField : regularFields) {
			builder.append(CURL_FIELD_TEMPLATE.replace("{field}", regularField + "=" + codeTemplates.fieldRegularValue() + " "));
		}
		
		for (String fileField : fileFields) {
			builder.append(CURL_FIELD_TEMPLATE.replace("{field}", fileField + "=@" + codeTemplates.fieldFileValue() + " "));
		}
		
		String result = builder.toString();
		
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		
		return result;
	}
}