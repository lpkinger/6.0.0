Ext.QuickTips.init();
Ext.define('erp.controller.oa.flow.Flow', {
	extend : 'Ext.app.Controller',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'oa.flow.Flow','oa.flow.FlowPanel','oa.flow.FlowRelativePanel','oa.flow.FlowViewPanel','oa.flow.FlowOperationPanel',
			  'oa.flow.FlowToolbar','core.trigger.DbfindTrigger','core.form.MultiField','oa.flow.FlowFieldPanel','core.form.FileField',
			  'core.form.MonthDateField','core.trigger.TextAreaTrigger','core.form.MultiField','core.form.FileField',
		   	'core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.form.MultiField',
		   		'core.grid.TfColumn', 'core.form.YnField','core.form.ConDateHourMinuteField','core.form.DateHourMinuteField',
		   		'core.grid.ItemGrid','core.form.FtDateField','core.form.ColorField','core.form.CheckBoxGroup','core.form.RadioGroup'],
	init : function() {
		var me = this;
		this.control({
			'combo[logic=Itemgrid]':{
				change:function(s){
					var Itemgrid = Ext.ComponentQuery.query('itemgrid');
					var baseCaller = Itemgrid[0].logic;
					
					Ext.Ajax.request({
						url:basePath + 'common/getFieldsDatas.action',
						async: false,
						params:{
							fields : "cd_detno as ig_item1,cd_varchar50_1 as ig_item2,cd_varchar50_2 as ig_item3,"+
								"cd_varchar50_3 as ig_item4,cd_varchar50_4 as ig_item5,cd_varchar50_5 as ig_item6," +
								"cd_varchar50_6 as ig_item7,cd_varchar50_7 as ig_item8",
							caller : 'customtable left join customtabledetail on CD_CTID=ct_id',
							condition : "ct_caller=(select ft_to from flow_transfer where " +
									"ft_caller='ComBoxPushItemGridData' and ft_from='"+baseCaller+"') and ct_varchar50_1='"+s.value+"'"
						},
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								showError(rs.exceptionInfo);return;
							}
							var rs = Ext.decode(rs.data);
							data = rs;
							var store = Ext.create('Ext.data.Store',{
								fields: ['IG_ITEM1','IG_ITEM2','IG_ITEM3','IG_ITEM4','IG_ITEM5','IG_ITEM6','IG_ITEM7','IG_ITEM8'],
								data: data
							})
							Itemgrid[0].store.loadData(data);
						}
					});
				}
			},
			'dbfindtrigger[name=fb_module]': {
 			   afterrender: function(f){
 				   f.onTriggerClick = function(){
 					   me.getModuleTree();
 				   };
 				   f.autoDbfind = false;
 			   }
 		   },
 		  'treepanel': {
			   itemmousedown: function(selModel, record){
				   var tree = selModel.ownerCt;
				   me.loadTree(tree, record);
			   }
		   },
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	getModuleTree: function(){
 	   var w = Ext.create('Ext.Window',{
		   title: '查找模板',
		   height: "100%",
		   width: "80%",
		   maximizable : true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   items: [{
			   anchor: '100% 100%',
			   xtype: 'treepanel',
			   rootVisible: false,
			   useArrows: true,
			   store: Ext.create('Ext.data.TreeStore', {
				   root : {
					   text: 'root',
					   id: 'root',
					   expanded: true
				   }
			   })
		   }],
		   buttons : [{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(btn){
				   btn.ownerCt.ownerCt.close();
			   }
		   },{
			   text: '确定',
			   iconCls: 'x-button-icon-confirm',
			   cls: 'x-btn-gray',
			   handler: function(btn){
				   var t = btn.ownerCt.ownerCt.down('treepanel');
				   if(!Ext.isEmpty(t.title)) {
					   Ext.getCmp('fb_module').setValue(t.title);
				   }
				   btn.ownerCt.ownerCt.close();
			   }
		   }]
	   });
	   w.show();
	   this.loadTree(w.down('treepanel'), null);
   },
   loadTree: function(tree, record){
	   var uStore = this.getUstore();
	   var pid = 0;
	   if(record) {
		   if (record.get('leaf')) {
			   return;
		   } else {
			   if(record.isExpanded() && record.childNodes.length > 0){
				   record.collapse(true, true);//收拢
				   return;
			   } else {
				   if(record.childNodes.length > 0){
					   record.expand(false,true);//展开
					   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
					   return;
				   }
			   }
		   }
		   pid = record.get('id');
	   }
	   tree.setLoading(true);
	   Ext.Ajax.request({
		   url : basePath + 'common/lazyTree.action?_noc=1',
		   params: {
			   parentId: pid,
			   condition: 'sn_using=1'
		   },
		   callback : function(options,success,response){
			   tree.setLoading(false);
			   var res = new Ext.decode(response.responseText);
			   if(record==null){
				   //防止子节点加载优软商城
				   
				   res.tree.push(uStore);
			   }
			   
			   if(res.tree){
				   if(record) {
					   record.appendChild(res.tree);
					   record.expand(false,true);//展开
					   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
				   } else {
					   tree.store.setRootNode({
						   text: 'root',
						   id: 'root',
						   expanded: true,
						   children: res.tree
					   });
				   }
			   } else if(res.exceptionInfo){
				   showError(res.exceptionInfo);
			   }
		   }
	   });
   },
   getUstore:function(){
	   var json = {};
	   Ext.Ajax.request({
		   url:basePath+'resource/uucloud/sysnavigation.json',
		   async:false,
		   success : function(response){
			   var text = response.responseText;
			   json = new Ext.decode(text);
		   }
	   });
	   return json;
   }
});