Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarStockProfit', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.BarStockProfit','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit', 'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	       'core.button.Banned','core.button.ResBanned','core.button.Post','core.button.ResPost','core.button.Query','core.button.GetPrice','core.button.Barcode','core.trigger.DbfindTrigger',
	       'core.button.BatchGenBarcode','core.button.PrintAll'
	       ],
	       init:function(){
	    	   var me = this;
	    	   var grid = Ext.getCmp('grid');
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({
	    		   'erpGridPanel2': { 
	    			   afterrender: function(grid){},
	    			   itemclick: function(selModel, record){    
	    			   	/* if(record.data.bsd_id != 0 && record.data.bsd_id != null && record.data.bsd_id != ''){
	    					   var btn = Ext.getCmp('barcodebtn');
	    					   	   btn && btn.setDisabled(false);
	    					   	   
	    				   }   		*/
	    				   this.GridUtil.onGridItemClick(selModel, record);
	    			   }
	    		   },
	    		 'dbfindtrigger[name=bsd_prodcode]':{
	    			focus: function(t){
	    				t.autoDbfind = false;
	    				t.setHideTrigger(false);
	    				t.setReadOnly(false); 	    			
	    				var bs_whcode  = Ext.getCmp("bs_whcode").value;	    				
	    				if (bs_whcode == ''|| bs_whcode == null){
	    					showError("请先选择仓库编号 !");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    					return;
	    				}
	    				/*var record = Ext.getCmp('grid').selModel.getLastSelected();
	    			    var batchcode = record.data['bsd_batchcode'];
	    			    if(batchcode !='' && batchcode != null){
	    			    	t.dbBaseCondition ="ba_whcode='"+bs_whcode+"' and ba_code='"+batchcode+"'";
	    			    }else{
	    			        t.dbBaseCondition ="ba_whcode='"+bs_whcode+"'";
	    			    }*/
	    			 }	    			
    		        },
    		        
    		      'dbfindtrigger[name=bsd_batchcode]':{
		    			focus: function(t){
		    				t.autoDbfind = false;
		    				t.setHideTrigger(false);
		    				t.setReadOnly(false); 	    			
		    				var bs_whcode  = Ext.getCmp("bs_whcode").value;	    				
		    				if (bs_whcode == ''|| bs_whcode == null){
		    					showError("请先选择仓库编号 !");
		    					t.setHideTrigger(true);
		    					t.setReadOnly(true);
		    					return;
		    				}
		    				var record = Ext.getCmp('grid').selModel.getLastSelected();
		    			    var prodcode = record.data['bsd_prodcode'];
		    			    if(prodcode !='' && prodcode != null){
		    			    	t.dbBaseCondition ="ba_whcode='"+bs_whcode+"' and ba_prodcode='"+prodcode+"'";
		    			    }else{
		    			        t.dbBaseCondition ="ba_whcode='"+bs_whcode+"'";
		    			    }
		    			 }	    			
    		        },
    		        /*'erpDeleteDetailButton': {
	    			   afterrender: function(btn){	    
	    				   btn.ownerCt.add({
	    						 xtype: 'erpBarcodeButton'
	    					});
	    			   }
	    		   },*/
	    		   'erpBarcodeButton':{
	    			   click: function(btn) {
	                   	   var barcodeinqty = false;
	                   	   var grid = Ext.getCmp("grid");
	    				   var linkCaller="profit";
	                       var id = Ext.getCmp("bs_id").value;
	                       var inoutNo=Ext.getCmp("bs_code").value;
	                       var pi_class=Ext.getCmp("bs_class").value;
	                       var status = Ext.getCmp("bs_status").value;
	                       var formCondition1 = "bs_idIS" + id +"and bs_codeIS '"+inoutNo+"'"+"and bs_classIS'"+pi_class+"'";
	                       var gridCondition1 = "bdd_bsidIS" + id +" order by bdd_id asc";
	                       var result = 0;
                           Ext.Ajax.request({
                               url : basePath + 'common/getFieldData.action',
                               async: false,
                               params: {
                                   caller: 'barstocktakingdetaildet',
                                   field: 'count(bdd_bsid)',
                                   condition: 'bdd_bsid=' + id
                               },
                               method : 'post',
                               callback : function(opt, s, res){
                                   var r = new Ext.decode(res.responseText);
                                   if(r.exceptionInfo){
                                       showError(r.exceptionInfo);return;
                                   } else if(r.success){
                                       result = r.data;
                                   }
                               }
                           });
	                      if(result > 0){  //存在已经生成的条码明细
	                    	  me.FormUtil.onAdd('addBarcode'+id, '条形码维护('+inoutNo+')', 'jsps/scm/reserve/profit.jsp?_noc=1&whoami=' 
		                    		  + linkCaller +'&key='+id+'&inoutno='+inoutNo+'&status='+status+'&formCondition=' + formCondition1 + '&gridCondition=' 
		                    		  + gridCondition1);
	                      }else{
	                    	  linkCaller="profitBarcode";
	                    	   formCondition1 = "bs_idIS" + id +" and bs_codeIS'"+inoutNo+"'"+"and bs_classIS'"+pi_class+"'";
	                           gridCondition1 = "bsd_bsidIS" + id+"and nvl((select sum(bdd_inqty) from barstocktakingdetaildet where bsd_id=bdd_bsdid),0)<bsd_inqty "+
	                          					" group by bsd_bsid,bsd_detno,bsd_id,bsd_prodcode,pr_detail,pr_tracekind,bsd_inqty,pr_zxbzs order by  bsd_detno asc";
                              var win = new Ext.window.Window({
                                  id: 'win',
                                  height: '80%',
                                  width: '90%',
                                  maximizable: true,
                                  title:'<span><font color=blue>条形码维护['+(formCondition1.split('IS')[formCondition1.split('IS').length-1]).replace("'","").replace("'","")+':'+inoutNo+']</font></span>',
                                  buttonAlign: 'center',
                                  layout: 'anchor',
                                  closeAction:'hide',
                                  items: [{
                                      tag: 'iframe',
                                      frame: true,
                                      anchor: '100% 100%',
                                      layout: 'fit',
                                      html: '<iframe id="iframe_' + linkCaller + '" src="' + basePath + 'jsps/scm/reserve/profitBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutNo+ '&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1 + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                  }]
                              });
                                  win.show();
	                         
                          }
	                   }
	    			  
	    		   },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				  var form = me.getForm(btn);
	    				  if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					  me.BaseUtil.getRandomNumber();//自动添加编号
	    				  }
	    				   me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value == 'DELETED'){
	    					   btn.hide();
	    				   }	    				   
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({pu_id: Number(Ext.getCmp('bs_id').value)});
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onUpdate(me);  			
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('add' + caller, '新增库存条码盘盈维护', "jsps/scm/reserve/barStockProfit.jsp?whoami=" + caller);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },	    
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }	    				
	    			   },
	    			   click: function(btn){
	    				   var grid = Ext.getCmp('grid');
	    				   me.FormUtil.onSubmit(Ext.getCmp('bs_id').value);  				
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){				    		
	    				    me.FormUtil.onAudit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   /*'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if((status && status.value != 'AUDITED') ){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },*/
	    		  'erpBatchGenBarcodeButton':{//根据批号：一个批号生成一个条码
		    			afterrender: function(btn){
		    				 var status = Ext.getCmp(me.getForm(btn).statuscodeField);
		    				if(status &&(status.value == 'AUDITED' || status.value =='已审核')){
			    				btn.hide();
			    			}
		    			},		
		    			click: function (btn){
		    				/*var r = me.getForm();
		    				var param = unescape(escape(Ext.JSON.encode(r)));*/		    						    				
		    				var bs_id =  Ext.getCmp("bs_id").value;
		    				me.FormUtil.setLoading(true);
					    	Ext.Ajax.request({
								url : basePath + "scm/reserve/barStockProfit/batchGenBarcode.action",			
								params: {     
									      caller: caller,
					 			          id:bs_id
					 			        },			
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										showError(res.exceptionInfo);return;
									}else {		
										showMessage('提示', '批量生成成功!', 1000);
										window.location.href = basePath +'jsps/scm/reserve/barStockCode.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+condition;
									}
								}
							})    		   
		    			}
		    		},
		    'erpPrintAllButton':{	
    			click:function (btn){
    				var  lps_barcaller = "BarStockPrint";
    				var win = new Ext.window.Window({
				    	id : 'win',			  
						maximizable : true,
					    buttonAlign : 'center',
					    layout : 'anchor',
					    title: '打印模板选择',
					    modal : true,
	   				    items: [{
	   				          tag : 'iframe',
				    	      frame : true,
				    	      anchor : '100% 100%',
				    	      layout : 'fit',
	   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lps_barcaller +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	   				    }]	   				         
	    	       });
	    	       win.show();	    
    			}
		    }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){}
	   
});