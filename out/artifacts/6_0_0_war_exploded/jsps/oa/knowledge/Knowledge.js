function openUrl(value, keyField, url, title) {
		var kind = Ext.getCmp('kl_kindid');
		if(kind){
		 kind=kind.value;
		}else{
		var data=Ext.getCmp('grid').getSelectionModel().lastFocused.data;
		 kind=data.kl_kindid;
		} 
		url = url + '?formCondition=' + keyField + "=" + value+ "&gridCondition=kc_klid=" + value+ "&mappingCondition=kl_kindid=" + kind + "And kl_idNO"+ value;
		var panel = Ext.getCmp(keyField + "=" + value);
		var main = parent.Ext.getCmp("content-panel");
		if (!panel) {
			if (title.toString().length > 4) {
				title = title.toString().substring(title.toString().length - 4);
			}
			panel = {
				title : title,
				tag : 'iframe',
				tabConfig : {
					tooltip : title + '(' + keyField + "=" + value + ')'
				},
				frame : true,
				border : false,
				layout : 'fit',
				iconCls : 'x-tree-icon-tab-tab',
				html : '<iframe id="iframe_maindetail_'+ keyField+ "_"+ value+ '" src="'+ basePath+ url+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
				closable : true,
				listeners : {
					close : function() {
						main.setActiveTab(main.getActiveTab().id);
					}
				}
			};
			openTab(panel, keyField + "=" + value);
		} else {
			main.setActiveTab(panel);
		}
	}
	function openTab(panel, id) {
		var o = (typeof panel == "string" ? panel : id || panel.id);
		var main = parent.Ext.getCmp("content-panel");
		/*var tab = main.getComponent(o); */
		if (!main) {
			main = parent.parent.Ext.getCmp("content-panel");
		}
		var tab = main.getComponent(o);
		if (tab) {
			main.setActiveTab(tab);
		} else if (typeof panel != "string") {
			panel.id = o;
			var p = main.add(panel);
			main.setActiveTab(p);
		}
	}
function openWin(){
    var win = new Ext.window.Window(
				{
					id : 'win',
					height : '350',
					width : '550',
					title:'知识查看申请',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeApply'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
}
	