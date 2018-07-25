/**
 * 点击按钮，弹出window，内嵌grid，可修改保存
 * button.caller,
 * button.condition传入条件
 */
Ext.define('erp.view.core.button.GridWin', {
	extend : 'Ext.Button',
	alias : 'widget.erpGridWinButton',
	iconCls : 'x-button-icon-detail',
	cls : 'x-btn-gray',
	text : '&nbsp;',
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	},
	setConfig: function(args) {
		if(args && args instanceof Object) {
			var me = this, keys = Ext.Object.getKeys(args);
			Ext.each(keys, function(k){
				me[k] = args[k];
				var f = me['set' + Ext.String.capitalize(k)];
				if(f && typeof f === 'function') {
					f.call(me, args[k]);
				}
			});
		}
	},
	handler: function(btn) {
		var win = this.win;
		if(!win) {
			var url = 'jsps/common/gridpage.jsp?whoami=' + btn.caller + '&gridCondition=' +
				btn.condition;
			var p = btn.parseParamConfig();
			if(p) {
				url += '&' + p;
			}
			win = this.createWin(url);
		}
		win.show();
		var me = this;
		if(!me.contentWin) {
			me.setContentWin();
		}
	},
	setContentWin : function() {
		var me = this, win = this.win;
		setTimeout(function(){
			var iframe = win.getEl().down('iframe');
			if(iframe) {
				me.contentWin = iframe.dom.contentWindow;
				if(me.contentWin.Ext) {
					var grid = me.contentWin.Ext.getCmp('grid');
					if ( grid ) {
						var saveBtn = grid.down('erpSaveButton');
						if(saveBtn) {
							saveBtn.on('beforesave', function(){
								return me.fireEvent('beforesave', me);
							});
							saveBtn.on('aftersave', function(){
								me.fireEvent('aftersave', me);
							});
						}
					}
				}
			}
		}, 2000);
	},
	createWin: function(url) {
		this.win = Ext.create('Ext.window.Window', {
			title: this.text,
			border : 0,
			height: '80%',
			width: '80%',
			closeAction: 'hide',
			items: [{
				height: '100%',
				width: '100%',
				html: '<iframe src="' + basePath + url + '" height="100%" width="100%"></iframe>'
			}]
		});
		return this.win;
	},
	parseParamConfig: function() {
		var p = this.paramConfig, u = [];
		if(p && p instanceof Object) {
			var keys = Ext.Object.getKeys(p);
			Ext.each(keys, function(k){
				u.push(k + '=' + encodeURIComponent(p[k]));
			});
		}
		return u.join('&');
	}
});