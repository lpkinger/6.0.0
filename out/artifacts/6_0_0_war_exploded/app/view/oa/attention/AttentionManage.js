Ext.define('erp.view.oa.attention.AttentionManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpAttentionManageGridPanel',  
	    	  anchor: '100% 100%',
	    	  dockedItems: [{
                 xtype: 'toolbar',
                 dock: 'top',
                 style:'font-size:16px;height:40px',
			     bodyStyle: 'font-size:16px;height:40px',
                 items: [{
	              xtype: 'button',
	              id:'delete',
                  iconCls: 'tree-delete',
		         text: $I18N.common.button.erpDeleteButton,
		         style:'margin-left:10px'
	              },{
	              xtype:'button',
	              text:'授权设置',
	              id:'accredit',
	              iconCls: 'x-button-icon-install',
	              style:'margin-left:10px'
	              },{
	              xtype:'button',
	              text:'关注点设置',
	              id:'attention',
	              iconCls: 'x-button-icon-install',
	              style:'margin-left:10px'
	              },{
	              xtype:'button',
	              text:'等级设置',
	              id:'rank',
	              iconCls: 'x-button-icon-install',
	              style:'margin-left:10px'
	              }]
	              }]
	             }] 
		});
		me.callParent(arguments); 
	}
});