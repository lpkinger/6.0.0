Ext.define('erp.view.plm.base.ProductType',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true,
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'erpProductTypeTreePanel',
				border:false,
				split:false,
				id:'producttype',
				icon:basePath + 'resource/images/tree/add2.png',
				region:'west',
				width:300,
				minWidth:300,
				tbar: [{
					xtype: 'button',
					text:'父类型',
					id:'addrootprokind',
					disabled:false,
					iconCls:'x-button-icon-addgroup'					
				},{
					xtype: 'button',
					text:'子类型',
					id:'addprokind',
					disabled:true,
					iconCls:'x-button-icon-addgroup'
				},{
					xtype:'button',
					text:'修改',
					id:'treeupdate',
					disabled:true,
					iconCls: 'tree-back',
					style:'margin-left:10px'
				},{
					xtype: 'button',
					id:'treedelete',
					disabled:true,
					iconCls: 'tree-delete',
					text: $I18N.common.button.erpDeleteButton,
					style:'margin-left:10px'
				}]
			},{
				region:'center',
				layout:'border',
				items:[{
					xtype: 'toolbar',
					region:'north',
					dock: 'top',
					style:'font-size:16px;height:40px',
					bodyStyle: 'font-size:16px;height:40px;background:#EBEBEB;',
					style:'background:#EBEBEB;height:40px;',
					layout:'column',
					items: [{
						id:'productname',					 
						hideLabel :true,
						readOnly:true,
						xtype:'textfield',
						columnWidth:1,
						style:'margin-left:10px;margin-top:8px',
						fieldStyle : 'background:#EBEBEB ;border-bottom-style: none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:green;border-left:none;font-weight:bold'
					},{
						xtype:'textfield',
						id:'productid',
						hidden:true,
						columnWidth:0
					}]
				},{
						xtype:'tabpanel',
						region:'center',
						layout:'fit',
						id:'tab',
						border:false,
						items:[{
							title:'产品阶段计划',
							xtype:'erpProjectPhaseView',
							region:'center',
							id:'erpProjectPhaseView',
							caller:'PrjPhase',
							layout:'fit',
							multiselected:null,
							keyField:'PH_ID_TEMP',
							bbar:[],
							selModel: Ext.create('Ext.selection.CheckboxModel',{
								ignoreRightMouseSelection : false,
								listeners:{
									selectionchange:function(selectionModel, selected, options){
	
										}
									}
								})	
						},
						{
							title:'产品文件',
							xtype:'container',
							layout:'border',
							id:'fileIndex',
							listeners:{
								activate:function(self){
									var tree = Ext.getCmp("indexTree");
									tree.setRootNode(tree);  //加载根节点
									
									var firstNode = tree.getStore().tree.root.childNodes[0];
									if(firstNode){
										tree.getSelectionModel().select(firstNode,false,false); //切换的时候默认选中第一个文件
									}							
								}
							},
							items:[
								{
									xtype:'erpFileTree',
									region:'west'
								},
								{
									xtype:'erpFileTreeGrid',
									region:'center'
								}
							]
						},
						{
							title:'产品任务书',
							multiselected:null,
							xtype: 'erpTaskBookTeamplateGrid'
						}
						]
					}]
				}]
		});
		me.callParent(arguments); 
	}
});