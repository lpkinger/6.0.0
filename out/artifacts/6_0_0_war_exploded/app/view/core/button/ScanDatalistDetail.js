/**
 * 自动获取编号按钮
 */	
Ext.define('erp.view.core.button.ScanDatalistDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScanDatalistDetail',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpScanDatalistDetail,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var me= this,grid=me.ownerCt.ownerCt;
			
			
			if(grid.lastSelectRecord){
				var s = grid.lastSelectRecord;
				
		    	var win = new Ext.window.Window({
			    	id : 'win',
			    	modal:'true',
   				    height: "50%",
   				    width: "50%",
   					buttonAlign : 'center',
   					layout : 'fit',
   				    items: [
   				            Ext.create('Ext.Panel',{
   				            	layout:'column',
   				            	heigh:"100%",
   				            	width:"100%",
                                defaults:{
                                	columnWidth:0.94,
                                	xtype:'textfield'
                                },				            
   				            	items:[{
   				            		fieldLabel:'物料编号',
   				            		name:'pr_code',
   				            		value:s.data.pr_code
   				            	},{
   				            		fieldLabel:'物料名称',
   				            		name:'pr_detail',
   				            		value:s.data.pr_detail
   				            	},{
   				            		xtype:'textareafield',
   				            		fieldLabel:'规格',
   				            		name:'pr_spec',
   				            		grow:true,
   				            		value:s.data.pr_spec
   				            	}]
   				            	
   				            })
   				            
   				      ]
   				});
   				win.show();
			}

		}
	});