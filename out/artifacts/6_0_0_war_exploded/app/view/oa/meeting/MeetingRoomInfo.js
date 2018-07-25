Ext.define('erp.view.oa.meeting.MeetingRoomInfo',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var mrid = getUrlParam('formCondition').split('IS')[1];//数组【0】【1】值
//		alert(mrid);
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{ 
					xtype:'tabpanel',
					anchor: '100% 100%',
                    items:[{
					   title:'会议室信息',	
					   iconCls:'workrecord-log' ,
					   xtype: 'erpFormPanel',
					   bbar: null,
					   frame:true,
					   anchor: '100% 100%',					
					   keyField: 'mr_id'		
					},{
                       title:'设备信息',
                       iconCls:'workrecord-task',
                       caller: 'Equipment!Query',
					   html: '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/meeting/equipment.jsp?whoami=Equipment&urlcondition=eq_mrid='+ mrid +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
					   height:300,
					   anchor: '100% 100%'
					}]
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});