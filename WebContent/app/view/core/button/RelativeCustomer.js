function relative(uu,cuid){
	var grid = Ext.getCmp('smallgrid');
	grid.setLoading(true);
	Ext.Ajax.request({//拿到grid的columns
    	url : basePath + "ac/updateCustomerData.action",
    	params:{
    	  id:cuid,
    	  uu:uu
    	},
    	method : 'post',
    	callback : function(options,success,response){
    		grid.setLoading(false);
    		var res = new Ext.decode(response.responseText);
    		if(res.exceptionInfo){
    			showError(res.exceptionInfo);return;
    		}
    		Ext.getCmp('erpRelativeCustomerButton').loadCustomerData();
    	}
    });
}
Ext.define('erp.view.core.button.RelativeCustomer',{
		extend: 'Ext.Button', 
		alias: 'widget.erpRelativeCustomerButton',
		id: 'erpRelativeCustomerButton',
		text: '关联现有供应商/客户',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 160,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler : function(btn){
			var uu = getUrlParam("uu");
			Ext.create('Ext.window.Window',{
	    		width:673,
	    		height:'65%',
	    		iconCls:'x-grid-icon-partition',
	    		id:'win',
	    		resizable : false,
	    		items:[{
	    			xtype:'form',
	    			layout:'column',
	    			region:'north',
	    			frame:true,
	    			items:[{
	    				xtype: 'textfield',
	    		        id: 'search',
	    			},{
	    				xtype: 'combo',
	                	store: Ext.create('Ext.data.Store', {
	                        fields: ['display', 'value'],
	                        data : [
	                            {"display": '企业名称', "value": '企业名称'},
	                            {"display": '营业执照号', "value": '营业执照号'}
	                        ]
	                    }),
	                    id : 'cb',
	                    editable: false,
	                    displayField: 'display',
	                    valueField: 'value',
	            		queryMode: 'local',
	            		value: '企业名称',
	    			}],
	    			buttonAlign:'center',
	    			buttons:[{
	    				xtype:'button',
	    				columnWidth:0.12,
	    				text:'搜索',
	    				width:60,
	    				iconCls: 'x-button-icon-save',
	    				margin:'0 0 0 30',
	    				handler:function(btn){
	    					var cb = Ext.getCmp('cb').value;
	    					var search =Ext.getCmp('search').value;
	    					var condition="";
	    					if(search){
	    						if(cb=='企业名称'){
	    							condition = "cu_name like '%"+search+"%' ";
	    						}else{
	    							condition = "cu_businesscode like '%"+search+"%' ";
	    						}
	    						Ext.getCmp('erpRelativeCustomerButton').loadCustomerData(condition);
	    					}else{
	    						Ext.getCmp('erpRelativeCustomerButton').loadCustomerData();
	    					}
	    					
	    				}
	    			},{
	    				xtype:'button',
	    				columnWidth:0.1,
	    				text:'取消',
	    				width:60,
	    				iconCls: 'x-button-icon-close',
	    				margin:'0 0 0 10',
	    				handler:function(btn){
	    					Ext.getCmp('win').close();
	    				}
	    			}]
	    		},{
	    		  xtype:'gridpanel',
	    		  region:'south',
	    		  id:'smallgrid',
	    		  layout:'fit',
	    		  height:'80%',
	    		  columnLines:true,
	    		  store:Ext.create('Ext.data.Store',{
						fields:[{name:'cu_id',type:'int'},{name:'cu_code',type:'string'},{name:'cu_name',type:'string'},{name:'cu_shortname',type:'string'},
								{name:'cu_businesscode',type:'string'},{name:'cu_contact',type:'string'},{name:'cu_mobile',type:'string'},{name:'cu_sellername',type:'string'}],
					    data:[]
	    		  }),
	    		  columns:[{
	    			  dataIndex:'cu_id',
	    			  header:'cu_id',
	    			  width:0
	    		  },{
	    			  dataIndex:'cu_code',
	    			  header:'编号',
	    			  width:0
	    		  },{
	    			  dataIndex:'cu_name',
	    			  header:'企业名称',
	    			  width:120
	    		  },{
	    			  dataIndex:'cu_shortname',
	    			  header:'简称',
	    			  width:80
	    		  },{
	    			dataIndex:'cu_businesscode',
	    			header:'营业执照号',
	  			  	width:100
	    		  },{
	    			dataIndex:'cu_contact',
	    			header:'联系人',
	    			width:80
	    		  },{
	    			dataIndex:'cu_mobile',
	    			header:'手机号',
	    			width:80
	    		  },{
	    		  	dataIndex:'cu_sellername',
	    		  	header:'业务员'
	    		  },{
	    			header:'操作', 
	    			width:100,
	    			renderer : function(meta,value,record){
	    				var cuid = record.get('cu_id');
	    				return "<input type='button' value='关联' onclick=relative('"+uu+"','"+cuid+"');>"
	    			}
	    		  }],
	    		  listeners: {//滚动条有时候没反应，添加此监听器
						/*viewready: function() {
							pageSize = Math.ceil(this.view.el.getHeight() / 66);
						},*/
						scrollershow: function(scroller) {
							if (scroller && scroller.scrollEl) {
								scroller.clearManagedListeners();  
								scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
							}
						}
	    		  }
	    		}]
	    	}).show();
	      //  this.loadVendorData();
		},
		loadCustomerData:function(condition){
			var grid=Ext.getCmp('smallgrid');
	        grid.setLoading(true);//loading...
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + "ac/getCustomerData.action",
	        	params:{
	        	  caller:'Customer',
	        	  condition:condition
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		grid.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo){
	        			showError(res.exceptionInfo);return;
	        		}
	        		var data = res.data; 
	        		 grid.store.loadData(data); 
	        		//自定义event
	        		grid.addEvents({
	        		    storeloaded: true
	        		});
	        		grid.fireEvent('storeloaded', grid, data);
	        	}
	        });
		},
});