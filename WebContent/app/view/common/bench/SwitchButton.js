//可选按钮组合
Ext.define('erp.view.common.bench.SwitchButton', {
	extend : 'Ext.container.Container',
	alias : [ 'widget.erpSwitchButton' ],
	cls : 'x-btn-switch',
	activeCls : 'x-btn-switch-active',
	listeners:{
		afterrender:function(){
			var me = this;
			Ext.Array.each(me.items.items, function(btn) {
				if (btn.active) {
					me.setActive(btn);
				}
				if(!btn.noactive){
					btn.on('click', function(b) {
						me.setActive(b);
					});
				}
			});			
		}
	},
	setActive : function(button) {
		var me = this;
		me.activeButton && (me.activeButton.removeCls(me.activeCls));
		if(button){
			me.activeButton = button;
			button.addCls(me.activeCls);
			if(!button.noactive){
				me.fireEvent('change', me, button);
			}
		}
	},
	getActive : function() {
		return this.activeButton;
	}
});