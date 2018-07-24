Ext.define('erp.view.plm.project.ProjectMessage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 	
		Ext.apply(me, { 
			items: [{			
						xtype:'tabpanel',
						region:'center',
						layout:'fit',
						id:'tab',
						items:[{
								title : '项目阶段计划',
								tag : 'iframe',
								id:'prjphase',
								border : false,
								layout : 'fit',
								iconCls : 'x-tree-icon-tab-tab',
								html : '<iframe id="a" src="'+basePath+'jsps/common/datalist.jsp?whoami=ProjectPhasePlan&urlcondition=pp_prjid='+prjid+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
								closable : true
							},{
								title : '项目甘特图',
								tag : 'iframe',
								id:'prjGant',
								border : false,
								layout : 'fit',
								iconCls : 'x-tree-icon-tab-tab',
								html : '<iframe id="a" src="'+basePath+'jsps/plm/task/gantt.jsp?formCondition='+formCondition+'&readOnly=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
								closable : true
							},{
								title : '项目文档',
								tag : 'iframe',
								id:'prjDoc',
								border : false,
								layout : 'fit',
								iconCls : 'x-tree-icon-tab-tab',
								html : '<iframe id="a" src="'+basePath+'jsps/plm/project/ProjectDoc.jsp?Condition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
								closable : true
							},{
								title : '项目变更信息',
								tag : 'iframe',
								id:'prjChange',
								border : false,
								layout : 'fit',
								iconCls : 'x-tree-icon-tab-tab',
								html : '<iframe id="a" src="'+basePath+'jsps/common/datalist.jsp?whoami=ProjectChange&urlcondition=pc_oldprjcode=\''+prjCode+'\'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
								closable : true
							},{
								title:'项目团队',
								id:'ProjectTeam',
								layout:'fit',
								frame:true,
								iconCls : 'x-tree-icon-tab-tab',
								html:'<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="'+basePath+'jsps/common/datalist.jsp?whoami=ProjectTeam&urlcondition=team_pricode=\''+prjCode+'\'"></iframe>',
								closable : true
							}
						]				
					}
			]
		}); 
		me.callParent(arguments); 
	}
});