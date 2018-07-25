Ext.define('erp.view.common.DeskTop.MoreInfo', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true, 
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{xtype: 'toolbar',  
				cls: 'moreinfo-toolbar',
				region: 'north',  
				anchor: '100% 7%', 
				items : [{
					xtype : 'button',
					text:'即时沟通',
					id:'contact',
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-chat',
					style: {
						marginRight: '10px'
					},
					width: 100
				},{
					xtype : 'button',
					text:'发布通知',
					id:'addNote',
					cls: 'x-btn-gray',
					iconCls: 'group-post',					        				
					style: {
						marginRight: '10px'
					},
					width: 100
				},{
					xtype : 'button',
					text:'发布新闻',
					id:'addNews',
					cls: 'x-btn-gray',
					iconCls:'x-button-icon-content',
					style: {
						marginRight: '10px'
					},
					width: 100
				}] }
			,{								
				xtype : 'erpDeskTabPanel',
				anchor: '100% 93%',
				cls:'top_tabbar',
				bodyStyle : {
					border : 'none'
				},
				bodyBorder : false,
				items : [{						
					title : '内部通知',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',
					id : 'Inform',
					keyField:'no_id',
					pfField:null,
					url:'jsps/oa/info/NoteR.jsp',
					caller :'Note',
					firstPage:true,
					defaultCondition : 'no_infotype=\'TZ\' and em_id='+em_id+' and (\''+em_type+'\'=\'admin\' or (no_id in (select  no_id from note  where no_approver=\''+em_type+'\' or no_ispublic=-1 or no_ispublic=1 or no_ispublic=0 and no_id in (SELECT  NO_ID FROM EMPSNOTES WHERE EMP_ID ='+em_id+'))))',
					showRowNum : false
				},{									
					title : '行政公告',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',				
					id : 'Notice',
					keyField:'no_id',
					pfField:null,
					url:'jsps/oa/info/NoteR.jsp',
					caller :'Note',	
					firstPage:true,
					defaultCondition :'no_infotype=\'GG\' and em_id='+em_id+' and (\''+em_type+'\'=\'admin\' or (no_id in (select  no_id from note  where no_approver=\''+em_type+'\' or no_ispublic=-1 or no_ispublic=1 or no_ispublic=0 and no_id in (SELECT  NO_ID FROM EMPSNOTES WHERE EMP_ID ='+em_id+'))))',
					dockedItems : [{
						id : 'pagingtoolbar3',
						xtype: 'erpDatalistToolbar',
						dock: 'bottom',
						displayInfo: true
					}],
					showRowNum : false,
					plugins : [Ext.create(
							'erp.view.core.grid.HeaderFilter'											
					), Ext.create('erp.view.core.plugin.CopyPasteMenu')] 
				},{						
					title : '时事新闻',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',
					id : 'News',
					keyField:'ne_id',
					pfField:null,
					url:'jsps/oa/news/NewsR.jsp',
					caller :'News',	
					firstPage:true,
					defaultCondition : '',
					dockedItems : [{
						id : 'pagingtoolbar2',
						xtype: 'erpDatalistToolbar',
						dock: 'bottom',
						displayInfo: true
					}],
					showRowNum : false,
					plugins : [Ext.create(
							'erp.view.core.grid.HeaderFilter'											
					), Ext.create('erp.view.core.plugin.CopyPasteMenu')]										
				}]
			}]
		});
		me.callParent(arguments);
	}

});