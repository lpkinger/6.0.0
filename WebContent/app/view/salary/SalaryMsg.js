Ext.define('erp.view.salary.SalaryMsg',{
	extend:"Ext.Viewport",
	layout: 'anchor', 
	hideBorders: true, 
	FormUtil:Ext.create('erp.util.FormUtil'),
	initComponent:function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				id:"salaryMsg",
				anchor:'100% 100%',
				frameHeader:'false',
				border:false,
				layout:"anchor",
				bodyStyle:{
					background:'#ffffff'
				},	
				/**
				 * 监听一些事件
				 * <br>
				 * Ctrl+Alt+S	单据配置维护
				 * Ctrl+Alt+P	参数、逻辑配置维护
				 */
				addKeyBoardEvents: function(){
					var me = this;
					Ext.EventManager.addListener(document.body, 'keydown', function(e){
						if(e.altKey && e.ctrlKey) {
							if(e.keyCode == Ext.EventObject.S) {
								var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
								forms = Ext.ComponentQuery.query('form'), 
								grids = Ext.ComponentQuery.query('gridpanel'),
								formSet = [], gridSet = [];
								if(forms.length > 0) {
									Ext.Array.each(forms, function(f){
										f.fo_id && (formSet.push(f.fo_id));
									});
								}
								if(grids.length > 0) {
									Ext.Array.each(grids, function(g){
										gridSet.push(g.caller || window.caller);
									});
									gridSet = Ext.Array.unique(gridSet);
								}
								if(formSet.length > 0 || gridSet.length > 0) {
									url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
								}
								//用于生成datalist 时要用的sn_lockpage
								var main = parent.Ext.getCmp("content-panel");
								if(!main){
									main = parent.parent.Ext.getCmp("content-panel");
								}
								if(main){
									main.lockPage = window.location.pathname.replace('/ERP/', '').split("?")[0];
								}
								me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
							} else if(e.keyCode == Ext.EventObject.P) {
								me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
							}
						}
					});
				},
			}] 
		});
		me.callParent(arguments); 	
	}
});