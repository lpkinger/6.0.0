Ext.define('erp.view.core.form.TimeMinuteField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.timeminutefield',
	requiers:['erp.view.core.picker.TimePicker'],
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
	regex :  /^(([01]?[0-9])|(2[0-3])):[0-5]?[0-9]$/,
	regexText : '格式不正确!',
	createMinutePicker : function() {
		var b = this, a = b.minutePicker;
		if (!a) {
			b.minutePicker = a = Ext.create("erp.view.core.picker.TimePicker", {
				renderTo : Ext.getBody(),
				floating : true,
				ownerCt : b,
				value:this.value,
				listeners : {
					scope : b,
					okclick : b.onOkClick,
					hourdblclick : b.onOkClick,
					minutedblclick : b.onOkClick
				}
			});
			
			a.alignTo(b.inputEl, 'tl-bl?');
		}
		return a;
	},
	onOkClick : function() {
		var vals = this.minutePicker.getValue();
		var a = vals[0], b = vals[1];
		if (vals.length == 2) {
			a = a == null ? new Date().getHours() : a;
			a = a < 10 ? '0' + a : a;
			b = b == null ? new Date().getMinutes() : b;
			b = b < 10 ? '0' + b : b;
			this.setValue(a + ':' + b);
		}
		this.fireEvent('afterChangeValue', this);
		this.minutePicker.hide();
	},
	setValue : function(value) {
		if (!this.regex.test(value)) {
			value=null;
		}
		this.callParent(arguments);
	},
	hasValid : function() {
		return this.regex.test(this.value);
	}
});