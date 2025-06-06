package io.onedev.server.model.support.issue.field.spec;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import io.onedev.server.annotation.Editable;
import io.onedev.server.buildspecmodel.inputspec.dateinput.DateInput;
import io.onedev.server.buildspecmodel.inputspec.dateinput.defaultvalueprovider.DefaultValueProvider;

@Editable(order=505, name=FieldSpec.DATE)
public class DateField extends FieldSpec {

	private static final long serialVersionUID = 1L;

	private DefaultValueProvider defaultValueProvider;
	
	@Editable(order=1000, name="Default Value", placeholder="No default value")
	@Valid
	public DefaultValueProvider getDefaultValueProvider() {
		return defaultValueProvider;
	}

	public void setDefaultValueProvider(DefaultValueProvider defaultValueProvider) {
		this.defaultValueProvider = defaultValueProvider;
	}

	@Override
	public String getPropertyDef(Map<String, Integer> indexes) {
		return DateInput.getPropertyDef(this, indexes, defaultValueProvider);
	}

	@Override
	public Object convertToObject(List<String> strings) {
		return DateInput.convertToObject(strings);
	}

	@Editable
	@Override
	public boolean isAllowMultiple() {
		return false;
	}

	@Override
	public List<String> convertToStrings(Object value) {
		return DateInput.convertToStrings(value);
	}

	@Override
	public long getOrdinal(String fieldValue) {
		if (fieldValue != null) 
			return Long.parseLong(fieldValue);
		else
			return super.getOrdinal(fieldValue);
	}

	@Override
	protected void runScripts() {
		if (getDefaultValueProvider() != null)
			getDefaultValueProvider().getDefaultValue();
	}

}
