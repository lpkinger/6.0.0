/**
 * color选择
 */
Ext.define('erp.view.core.form.ColorField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.colorfield',
	triggerCls : 'x-form-color-trigger',
	triggerTip : '选择颜色',
	onTriggerClick : function() {
		var me = this;
		if (!me.pickerShow) {
			var picker = me.picker = me.createPicker();
			picker.alignTo(me.inputEl, 'tl-bl?');
			picker.show();
			me.pickerShow = true;
		} else {
			me.picker.hide();
			me.pickerShow = false;
		}
	},
	createPicker : function() {
		var me = this;
		var picker = Ext.create('Ext.picker.Color', {
			value : '993300',
			floating : true,
			ownerCt : me,
			focusOnShow : true,
			renderTo : Ext.getBody(),
			style : {
				backgroundColor : "#fff"
			},
			listeners : {
				scope : this,
				select : function(picker, selColor) {
					me.inputEl.setStyle({
						backgroundColor : '#' + selColor
					});
					me.setValue(selColor);
					picker.hide();
					me.pickerShow = false;
				},
				show : function(field, opts) {
					field.getEl().monitorMouseLeave(500, field.hide, field);
				}
			}
		});
		return picker;
	},
	listeners : {
		afterrender : function() {
			if (this.value)
				this.inputEl.setStyle({
					backgroundColor : '#' + this.value
				});
		}
	}
});