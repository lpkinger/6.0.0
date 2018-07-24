Ext.define('erp.view.core.form.OperatorField', {
	extend: 'Ext.form.field.Text',
	alias : 'widget.operatorfield',
	operator : '>',
	inputValue : null,
	dataType: 'number',
	cls: 'x-form-field-operator',
	fieldSubTpl: [ 
	              '<label id="{cmpId}-operator" class="{operatorCls}" role="presentation">{operator}</label>',
                  '<input id="{id}" type="text" style="width: 100%;" {inputAttrTpl}',
	                   ' size="1"',
	                   '<tpl if="name"> name="{name}"</tpl>',
	                   '<tpl if="value"> value="{value}"</tpl>',
	                   '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
	                   '{%if (values.maxLength !== undefined){%} maxlength="{maxLength}"{%}%}',
	                   '<tpl if="readOnly"> readonly="readonly"</tpl>',
	                   '<tpl if="disabled"> disabled="disabled"</tpl>',
	                   '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
	                   '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
	                   ' class="{fieldCls} {typeCls} {editableCls} {inputCls}" autocomplete="off"/>'
              ],
	initComponent : function() {
		var me = this;
		me._getValue = Ext.form.field.Base.prototype.getValue;
		me.subTplData = me.subTplData || {};
    	Ext.apply(me.subTplData, {
    		operator: me.operator,
    		operatorCls: me.cls + '-label'
    	});
    	me.parseDataType();
    	me.callParent(arguments);
	},
	parseDataType: function() {
		this.dataType = {
			'number': 'number',
			'numbercolumn': 'number'
		}[this.dataType] || 'string';
		// date type does not support, use OperatorDateField please
	},
	sqlChars: {
		'<': '<',
		'≤': '<=',
		'>': '>',
		'≥': '>=',
		'≠': '<>',
		'=': '='
	},
	filterChars: {
		'<': 'lt',
		'≤': 'lte',
		'>': 'gt',
		'≥': 'gte',
		'≠': 'ne',
		'=': 'eq'
	},
	getValue : function() {
		var me = this, val = me.callParent();
		return Ext.isEmpty(val) ? null : (me.sqlChars[me.operator] + me.getQueryValue(val));
	},
	getQueryValue: function(val) {
		switch(this.dataType) {
		case 'string':
			val = "'" + val + "'";
			break;
		}
		return val;
	},
	getFilter: function() {
    	var me = this, val = me._getValue(), chr = me.filterChars[me.operator], filter = {};
    	if (!Ext.isEmpty(val)) {
    		filter[chr] = val;
    		return filter;
    	}
    }
});