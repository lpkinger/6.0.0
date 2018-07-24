/**
 * 自定义grid.column
 * tf即true/false,显示为是和否,其实际值对应T和F
 */
Ext.define('erp.view.core.grid.TfColumn', {
	extend : 'Ext.grid.column.Column',
	alias : [ 'widget.tfcolumn' ],
	trueText : $I18N.common.form.yes,
	falseText : $I18N.common.form.no,
	constructor : function(cfg) {
		this.callParent(arguments);
		this.editor = {
			xtype : 'combo',
			store : Ext.create('Ext.data.Store', {
				fields : [ 'display', 'value' ],
				data : [ {
					"display" : $I18N.common.form.yes,
					"value" : 'T'
				}, {
					"display" : $I18N.common.form.no,
					"value" : 'F'
				} ]
			}),
			editable: false,
			displayField : 'display',
			valueField : 'value',
			queryMode : 'local',
			value : 'F',
			hideTrigger : false,
			listeners: {
    			scope: this,
    			'change': function(c){
    				if(c.rawValue != this.trueText && c.rawValue != this.falseText){
    					//实现grid单元格编辑模式下，不让用户编辑combo
    					if(contains(c.rawValue, this.falseText, true)){
        					c.setValue(this.falseText);
        				} else if(contains(c.rawValue, this.trueText, true)){
        					c.setValue(this.trueText);
        				} else {
        					c.setValue(this.falseText);
        				}
    				}
    			}
    		}
		};
		var trueText = this.trueText, falseText = this.falseText;
		this.renderer = function(value) {
			if (value === undefined) {
				return falseText;
			}
			if (!value || value == 'F') {
				return falseText;
			}
			return trueText;
		};
	}
});
Ext.data.Types.TF = {
	convert : function(v, data) {
		if(!v || v == '')
			v = 'F';
		return v;
	},
	sortType : function(v) {
		return v.Latitude;
	},
	type : 'tf'
};