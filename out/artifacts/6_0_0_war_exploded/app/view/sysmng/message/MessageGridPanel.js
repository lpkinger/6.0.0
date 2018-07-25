Ext.define('erp.view.sysmng.message.MessageGridPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.messagegrigpanel', 
	id:'messagegrigpanel',
	layout:'card',
	bodyBorder: true,
	border: false,
	autoShow: true, 
	items: [
	{
		xtype:'tabpanel',
		id:'content-panel',		
		items: [
		{			
	  		xtype: 'component',
			id:'iframe',
			title:'列表',
			tabConfig:{tooltip:"详细"},
			autoEl: {
					
					tag: 'iframe',
					id:'datalistframe',
					style: 'height: 100%; width: 100%; border: none;',
					src:basePath +'jsps/common/datalist.jsp?whoami=Message'
					}
	  		}
		],
		listeners:{
			tabchange:function(){									
					if(this.activeTab.id=='iframe'){					
						var iframe = window.frames['iframe'].contentWindow||window.frames['iframe'].window;			
						if (iframe.Ext) {
								var grid=iframe.Ext.getCmp('grid');
									grid.getCount()
			    				}  
					}
					
				}
		}
	}
	],
	initComponent : function(){ 
		var me=this;
		   
		this.callParent(arguments);
	},
	


	
});