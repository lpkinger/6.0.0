/**
 * 订单异动分析按钮
 */	
Ext.define('erp.view.core.button.OdDynamicAnalysis',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOrderAnalysisButton',
    	id:'oddynamicanalysis',
    	text: $I18N.common.button.erpOrderAnalysisButton,    
    	cls: 'x-btn-gray',
    	requires:['erp.util.BaseUtil'],
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		
		listeners:{
		  click:function(btn){
    			 var id=Ext.getCmp('mm_id').getValue();   			
    			 var  OrderWin = new Ext.window.Window({
    					id : 'OrderWin',
    					title: "订单异动分析",
    					height: "70%",
    					width: "93%",
    					maximizable : false,
    					closeAction:'destroy',
    					buttonAlign : 'center',
    					autoScroll:true,
    					layout : 'anchor',    			
    						listeners:{
    							beforeshow: function(win){
    								var values='';   							
    								values+=id+",";   								
    								Ext.Ajax.request({
    								     url:basePath+'pm/mps/getMaxCode.action',
    								     params:{
    								       code:id
    								     },
    								     method : 'post',
        	                            callback : function(options,success,response){
        	                               var res = new Ext.decode(response.responseText);
        	                               if(res.data)
        	                                {   values+=res.data;
        	                                  Ext.getCmp('oddynamicanalysis').getGridColumnsAndStore(win,values);
        	                                 }
        	                               }        	                               								         									
    								})
    			    				
    			    			}	
    						},   					
					        items:[{					        
					            xtype: 'grid',
					            loadMask:'true',
					        	columns: [{
					        		text: '物料编号',
					        		cls: 'x-grid-header-1',
					        		dataIndex:'MD_PRODCODE',
					        		width: 130
					        	},{
					        		text: '物料名称',
					        		cls: 'x-grid-header-1',
					        		dataIndex: 'PR_DETAIL',
					        		width: 110
					        	},{
					        		text: '规格',
					        		cls: 'x-grid-header-1',
					        		dataIndex: 'PR_SPEC',
					        		width: 100
					        	},{
					        		text: '订单编号',
					        		cls: 'x-grid-header-1',					        	
					        		dataIndex: 'MD_ORDERCODE',
					        		width: 100
					        	},{
					        		text: '订单序号',
					        		cls: 'x-grid-header-1',					        	
					        		dataIndex: 'MD_ORDERDETNO',
					        		width: 80
					        	},{
					        		text: '订单类型',
					        		cls: 'x-grid-header-1',					        		
					        		dataIndex: 'MD_ORDERKIND',
					        		width: 90
					        	},{
					        		text: '差异数',
					        		cls: 'x-grid-header-1',					        		
					        		dataIndex: 'DIFFQTY',
					        		width: 100
					        	},{
					        		text: '差异描述',
					        		cls: 'x-grid-header-1',				        		
					        		dataIndex: 'CHANGEKIND',
					        		width: 100
					        	},{
					        		text: '本次计划数',
					        		cls: 'x-grid-header-1',			        		
					        		dataIndex: 'QTY1',
					        		width: 100
					        	},{
					        		text: '上次计划数',
					        		cls: 'x-grid-header-1',					        		
					        		dataIndex: 'QTY2',
					        		width: 100
					        	}],
					        	columnLines: true,
					        	store: new Ext.data.Store({
					        		fields: ['MD_PRODCODE', 'PR_DETAIL', 'PR_SPEC','MD_ORDERCODE','MD_ORDERDETNO','MD_ORDERKIND','DIFFQTY','CHANGEKIND','QTY1','QTY2'],
					        		data: [{},{},{},{},{},{},{},{},{},{},{},{}]					         
					        	})					        					       
					        }],
    					bbar: ['->',{
    						text:'导出',
    						iconCls: 'x-button-icon-excel',
    						cls: 'x-btn-gray',
    						handler: function(btn){ 
    							var grid=Ext.getCmp('OrderWin').down('grid');//.getGrid();
    							var BaseUtil=Ext.create('erp.util.BaseUtil');
                                BaseUtil.exportGrid(grid,'订单异动分析');   							
    						}
    						},{
    						text:'关闭',
    						cls: 'x-btn-gray',
    						iconCls: 'x-button-icon-close',
    						listeners: {
    							click: function(){
    								OrderWin.close();
    							}
    						}
    					},'->']
    				});				  
    			 OrderWin.show();
		      }
		},
		 getGridColumnsAndStore: function(view,values){		
			 var me = this;	
			 Ext.getCmp('oddynamicanalysis').setLoading(true,view);
			 Ext.Ajax.request({//拿到grid的columns
	        	url : basePath+"pm/mps/OrderAnalysis.action",
	        	params: {
	        		caller: 'OrderAnalysis', 
	        		condition: values
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){       		
	        		var res = new Ext.decode(response.responseText);  
	        		Ext.getCmp('oddynamicanalysis').setLoading(false,view);
					if(res.data == null){
						showError("异动分析单没有数据！");
						Ext.getCmp('OrderWin').close();
					}else{
		        		 view.down('grid').store.loadData(res.data);  	        		
					}
	        		if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}    	    					          		
	        	}
          });	
	},
	
	setLoading : function(b,view) {
		var mask = this.mask;
		if (this.rendered) {
            Ext.destroy(this.mask);
            this.mask = null;
            }
		if(!mask){
			this.mask = mask = new Ext.LoadMask(view.down('grid'), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}	
		if (b)
			mask.show();
		else
			mask.hide();			
	}
    			
	});