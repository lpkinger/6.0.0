Ext.define('erp.view.core.form.OperatorDateField', {
	extend: 'Ext.form.field.Date',
	alias : 'widget.operatordatefield',
	operator : '>',
	inputValue : null,
	format: 'Y-m-d',
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
		me._getValue = Ext.form.field.Date.prototype.getValue;
		me.subTplData = me.subTplData || {};
    	Ext.apply(me.subTplData, {
    		operator: me.operator,
    		operatorCls: me.cls + '-label'
    	});
    	me.callParent(arguments);
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
		return Ext.isEmpty(val) ? null : (me.sqlChars[me.operator] + 
				"to_date('" + Ext.Date.format(val,'Y-m-d') + "','yyyy-mm-dd')");
	},
	getFilter: function() {
    	var me = this, val = me._getValue(), chr = me.filterChars[me.operator], filter = {};
    	if (!Ext.isEmpty(val)) {
    		filter[chr] = Ext.Date.format(val,'Y-m-d');
    		return filter;
    	}
    }
});