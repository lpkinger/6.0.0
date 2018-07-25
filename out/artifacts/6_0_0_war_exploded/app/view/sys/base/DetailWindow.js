Ext.define('erp.view.sys.base.DetailWindow',{
	extend: 'Ext.window.Window',
	alias: 'widget.detailwindow',
	title: '基本窗口',
	modal: true,
	header: {
		titlePosition: 0,
		titleAlign: 'center'
	},
	closable: true,
	closeAction: 'destroy',
	width: 600,
	minWidth: 350,
	height: 350,
	layout: {
		type: 'fit',
		padding: 1
	},
	initComponent : function(){ 
		this.callParent(arguments);
	},
	showRelyBtn:function(win,button){
		var el=button.getEl();
		button.getEl().dom.disabled = true;
		if (win.isVisible()) {
			win.hide(el, function() {
				el.dom.disabled = false;
			});
		} else {
			win.show(el, function() {
				el.dom.disabled = false;
				Ext.getBody().disabled=true;
			});
		}
	}
});