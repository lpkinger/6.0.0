/**
 * 转出货按钮
 */
Ext.define('erp.view.core.button.TurnProdIO', {
	extend : 'Ext.Button',
	alias : 'widget.erpTurnProdIOButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTurnProdIOButton,
	style : {
		marginLeft : '10px'
	},
	width : 110,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners : {
		click : function(btn) {
			btn.turn('SendNotify!ToProdIN!Deal', 'snd_snid=' + Ext.getCmp('sn_id').value
					+ ' and nvl(snd_yqty,0) < nvl(snd_outqty,0) and snd_statuscode=\'AUDITED\'',
					'scm/sale/turnProdOut.action?type=ProdInOut!Sale');
		}
	},
	turn : function(nCaller, condition, url) {
		var win = new Ext.window.Window(
				{
					id : 'win',
					height : "100%",
					width : "80%",
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_' + caller + '" src="' + basePath
								+ 'jsps/common/editorColumn.jsp?caller=' + nCaller + "&condition=" + condition
								+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],
					buttons : [
							{
								name : 'confirm',
								text : $I18N.common.button.erpConfirmButton,
								iconCls : 'x-button-icon-confirm',
								cls : 'x-btn-gray',
								listeners : {
									buffer : 500,
									click : function(btn) {
										var grid = Ext.getCmp('win').items.items[0].body.dom
												.getElementsByTagName('iframe')[0].contentWindow.Ext
												.getCmp("editorColumnGridPanel");
										btn.setDisabled(true);
										grid.updateAction(url);
										window.location.reload();
									}
								}
							}, {
								text : $I18N.common.button.erpCloseButton,
								iconCls : 'x-button-icon-close',
								cls : 'x-btn-gray',
								handler : function() {
									Ext.getCmp('win').close();
								}
							} ]
				});
		win.show();
	}
});