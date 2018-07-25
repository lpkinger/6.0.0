//可选按钮组合
Ext.define('erp.view.core.button.SwitchButton', {
	extend : 'Ext.container.Container',
	alias : [ 'widget.erpSwitchButton' ],
	cls : 'x-btn-switch',
	activeCls : 'x-btn-switch-active',
	listeners:{
		afterrender:function(){
			var me = this;
			Ext.Array.each(me.items.items, function(btn) {
				if (btn.active) {
					me.setActive(btn, false);
				}
				btn.on('click', function(b) {
					me.setActive(b, true);
				});
			});			
		}
	},
	setActive : function(button, ready) {
		var me = this;
		me.activeButton && (me.activeButton.removeCls(me.activeCls));
		me.activeButton = button;
		button.addCls(me.activeCls);
		ready && (me.fireEvent('change', me, button));
	},
	getActive : function() {
		return this.activeButton;
	}
});