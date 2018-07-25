Ext.define('erp.view.oa.attention.AddressBook',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true,
	listeners: {
            click: {
             element: 'el',
             fn: function(){
                var menu=Ext.getCmp('mainmenu');
                if(menu){
                 menu.close();
                }
               }
              }
            },      
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [
			{
			 xtype:'panel',
			 id:'view1',
			 layout: 'anchor',
			 items:[{
			    anchor: '100% 8%',
			    xtype:'toolbar',
			    id:'1',
			    defaults:{
			     scale:'medium'
			    },
			    items: [{
                xtype: 'button',
                text: '我的通讯录',
                id:'button1',               
                handler:function(btn){
                 gridid='AttentionGridPanel';
                 Ext.getCmp('first').show();
                 btn.setDisabled(true);
                 this.addCls('btn-basecls');
                 Ext.getCmp('second').hide();
                 Ext.getCmp('third').hide();
                 Ext.getCmp('button2').setDisabled(false);
                 Ext.getCmp('button2').removeCls('btn-basecls');
                 Ext.getCmp('button3').setDisabled(false);
                 Ext.getCmp('button3').removeCls('btn-basecls');
                },
                 listeners:{
                 afterrender:function(btn){
                  btn.setDisabled(true);
                 this.addCls('btn-basecls');
                 }
                }          
                },{
                xtype: 'button', 
                text: '公共联系人',
                id:'button2',
                style:'margin-left:10px;',
                 handler:function(btn){
                 gridid='PublicAddressBook';
                 btn.setDisabled(true);
                 this.addCls('btn-basecls');
                 Ext.getCmp('first').hide();
                 Ext.getCmp('third').hide();
                 Ext.getCmp('button1').setDisabled(false);
                 Ext.getCmp('button1').removeCls('btn-basecls');
                 Ext.getCmp('button3').setDisabled(false);
                 Ext.getCmp('button3').removeCls('btn-basecls');          
                 Ext.getCmp('second').show();
                 Ext.getCmp('publicqueryform').show(); 
                 Ext.getCmp('PublicAddressBook').show();
                }, 
               },{
                 xtype:'button', 
                 id:'button3',                
                 text: '内部通讯录',
                 style:'margin-left:10px;',
                 handler:function(btn){
                 gridid='employeeAddressBook';
                 Ext.getCmp('first').hide();
                 Ext.getCmp('second').hide();
                 btn.setDisabled(true);
                 this.addCls('btn-basecls');
                 Ext.getCmp('second').hide();
                 Ext.getCmp('third').hide();
                 Ext.getCmp('button1').setDisabled(false);
                 Ext.getCmp('button1').removeCls('btn-basecls');
                 Ext.getCmp('button2').setDisabled(false);
                 Ext.getCmp('button2').removeCls('btn-basecls');
                 Ext.getCmp('third').show();
                 Ext.getCmp('addrbook').show(); 
                 Ext.getCmp('employeeAddressBook').show();
                },
                
                },'->',{
                  xtype:'textfield',
                  emptyText:'搜索联系人',
                  style:'margin-right:20;',
                  id:'search',
                  height:24, 
                  width:200,
                  fieldStyle:'background:#CFCFCF;'                
                }]			    
			 },{
			   anchor: '100% 92%',
			   layout: 'border',
			    id:'first',
			    items:[{
				   region: 'center',       
	    	       xtype:'erpAttentionGridPanel',  	    	    
	              },{
			        xtype:'erpPersonalAddressTreePanel',
					region:'west',
                    layout:'anchor',
				    }]
			 },{
			    anchor: '100% 92%',
			    layout: 'border',
			    id:'second',
			    region:'south',
			    hidden:true,
	            items:[{
			     xtype:'erpAddressQueryFormPanel', 
			     caller:'PublicAddressBook',
			     hidden:true,
			      id:'publicqueryform',
			      region:'north',
			      height:'20%'		     	    
			    },{
			     xtype:'PublicAddressGridPanel', 
			     caller:'PublicAddressBook',
			     id:'PublicAddressBook',
			     hidden:true,
			     autoScroll : true,
			     region:'center',
			     condition:'as_sharedemid='+emid,	
			    }]  	    
			 },{
			   anchor: '100% 92%',
			    hidden:true,
			    id:'third',
			    layout: 'border',
			    items:[{
				  region: 'center',       
			     xtype:'PublicAddressGridPanel', 
			     caller:'EmployeeAddressBook',
			     layout:'fit',
			     id:'employeeAddressBook',
			     hidden:true,
			     condition:'1=1',
			     autoScroll : true,			    
	              },{
			        xtype:'erpEmployeeTreePanel',
			        id:'addrbook',
					region:'west',
					width:'20%',
                    layout:'anchor',
                    hidden:true,
				    }]
 	    
			 }]
	          }] 
		});
		me.callParent(arguments); 
	}
});