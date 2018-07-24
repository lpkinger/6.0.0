Ext.define('erp.view.core.form.MultiHourField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.multihourfield',
	requiers:['erp.view.core.picker.MultiHourPicker'],
	triggerCls : Ext.baseCSSPrefix + "form-minute-trigger",
	initComponent : function() {
		this.callParent(arguments);
		this.addEvents({
			afterChangeValue : true
		});
	},
	height:22,
	onTriggerClick : function() {
		var me = this;		
		if (this.minutePicker && !this.minutePicker.hidden) {
			this.minutePicker.hide();
			return;
		}
		this.createMinutePicker().show();
	},
	createMinutePicker : function() {
		var b = this, a = b.minutePicker;
		if (!a) {
			b.minutePicker = a = Ext.create("erp.view.core.picker.MultiHourPicker", {
				renderTo : Ext.getBody(),
				floating : true,
				ownerCt : b,
				value:this.value,
				listeners : {
					scope : b,
					okclick : b.onOkClick,
					hourdblclick : b.onOkClick,
				}
			});
			a.alignTo(b.inputEl, 'tl-bl?');
		}
		return a;
	},
	onOkClick : function() {
		var vals = this.minutePicker.getValue();
		this.setValue(vals);
		this.fireEvent('afterChangeValue', this);
		this.minutePicker.hide();
	},
	setValue : function(value) {
		this.callParent(arguments);
	}
});