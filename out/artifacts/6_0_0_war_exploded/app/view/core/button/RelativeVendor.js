function relative(uu,veid){
	var grid = Ext.getCmp('smallgrid');
	grid.setLoading(true);
	Ext.Ajax.request({//拿到grid的columns
    	url : basePath + "ac/updateVendorData.action",
    	params:{
    	  id:veid,
    	  uu:uu
    	},
    	method : 'post',
    	callback : function(options,success,response){
    		grid.setLoading(false);
    		var res = new Ext.decode(response.responseText);
    		if(res.exceptionInfo){
    			showError(res.exceptionInfo);return;
    		}
    		Ext.getCmp('erpRelativeVendorButton').loadVendorData();
    	}
    });
}
Ext.define('erp.view.core.button.RelativeVendor',{
		extend: 'Ext.Button', 
		alias: 'widget.erpRelativeVendorButton',
		id: 'erpRelativeVendorButton',
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
	    							condition = "ve_name like '%"+search+"%' ";
	    						}else{
	    							condition = "ve_webserver like '%"+search+"%' ";
	    						}
	    						Ext.getCmp('erpRelativeVendorButton').loadVendorData(condition);
	    					}else{
	    						Ext.getCmp('erpRelativeVendorButton').loadVendorData();
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
						fields:[{name:'ve_id',type:'int'},{name:'ve_code',type:'string'},{name:'ve_name',type:'string'},{name:'ve_shortname',type:'string'},
								{name:'ve_webserver',type:'string'},{name:'ve_contact',type:'string'},{name:'ve_tel',type:'string'},{name:'ve_buyername',type:'string'}],
					    data:[]
	    		  }),
	    		  columns:[{
	    			  dataIndex:'ve_id',
	    			  header:'ve_id',
	    			  width:0
	    		  },{
	    			  dataIndex:'ve_code',
	    			  header:'编号',
	    			  width:0
	    		  },{
	    			  dataIndex:'ve_name',
	    			  header:'企业名称',
	    			  width:120
	    		  },{
	    			  dataIndex:'ve_shortname',
	    			  header:'简称',
	    			  width:80
	    		  },{
	    			dataIndex:'ve_webserver',
	    			header:'营业执照号',
	  			  	width:100
	    		  },{
	    			dataIndex:'ve_contact',
	    			header:'联系人',
	    			width:80
	    		  },{
	    			dataIndex:'ve_tel',
	    			header:'手机号',
	    			width:80
	    		  },{
	    		  	dataIndex:'ve_buyername',
	    		  	header:'业务员'
	    		  },{
	    			header:'操作', 
	    			width:100,
	    			renderer : function(meta,value,record){
	    				console.log(record);
	    				var veid = record.get('ve_id');
	    				return "<input type='button' value='关联' onclick=relative('"+uu+"','"+veid+"');>"
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
		loadVendorData:function(condition){
			var grid=Ext.getCmp('smallgrid');
	        grid.setLoading(true);//loading...
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + "ac/getVendorData.action",
	        	params:{
	        	  caller:'Vendor',
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