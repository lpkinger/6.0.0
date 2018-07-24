Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SetLabelTemplate', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.SetLabelTemplate','core.grid.Panel2','scm.reserve.setLabelTemplate.LabelForm','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	       'core.button.Banned','core.button.ResBanned','core.button.Parameter','core.button.LabelPreview'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({
	    		   'erpGridPanel2': { 
	    			   itemclick: this.onGridItemClick
	    		   },	  
	    		   'erpParameterButton':{
	    		   	  click : function(btn){
	    		   	  	var id = Ext.getCmp("la_id").value;
	    		   	  	var formCondition = ' ';
	    				var linkCaller='LabelTemplateBarcode';
	    		   	  	var win = new Ext.window.Window(
    							{  
    								id : 'win',
    						    	height : '55%',
    								width : '70%',
    								maximizable : true,
    								buttonAlign : 'center',
    								layout : 'anchor',
    								items : [ {
    									tag : 'iframe',
    									frame : true,
    									anchor : '100% 100%',
    									layout : 'fit',
    									 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/scm/reserve/LabelParameter.jsp?_noc=1&whoami='+linkCaller+'&lp_laid='+id+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    								} ],
    							listeners:{                              
    							 'beforeclose':function(view,opt){//更新参数刷新界面避免缓存造成预览重叠
								    formCondition = getUrlParam('formCondition');
									gridCondition = getUrlParam('gridCondition');
									formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
									gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
	    							 window.location.href = basePath +'jsps/scm/reserve/setLabelTemplate.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+gridCondition;	    					
	                                }	
    							}
					     });
					  win.show(); 
				    },
				    afterrender: function(btn){
				     var form = me.getForm(btn);
    				  var status = Ext.getCmp(form.statuscodeField); 
    				  if(status && status.value != "ENTERING"){
    				  	     btn.hide();
    				  }
				    }
	    		   },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    			    	/*var value = Ext.getCmp("la_sql").value;
	    			      	value = value.toLowerCase();
						    if(value.indexOf("select") == -1 || value.indexOf("from") == -1){
						    	showError("取值SQL语句不合法！");
						    	return ;
						    }		*/				
	    			      me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {	    			
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('la_id').value)});
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
	    			     this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('add' + caller, '新增模板打印设置', "jsps/scm/reserve/setLabelTemplate.jsp?whoami=" + caller);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
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
	    			      me.FormUtil.onAudit(Ext.getCmp('la_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if((status && status.value != 'AUDITED')){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('la_id').value);
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
	    			   	me.FormUtil.onSubmit(Ext.getCmp('la_id').value);
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
	    			   	 me.FormUtil.onResSubmit(Ext.getCmp('la_id').value);
	    			   }
	    		   },
	    		   'erpBannedButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value == 'DISABLE'){
								btn.hide();
							}    				  
	    			   },
	    			   click: function(btn){
	    			   	this.FormUtil.onBanned(Ext.getCmp('la_id').value);
	    			   }
	    		   },
	    		   'erpResBannedButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				  if(status && status.value != 'DISABLE'){
								btn.hide();
							}	    				  
	    			   },
	    			   click: function(btn){
	    			   	 this.FormUtil.onResBanned(Ext.getCmp('la_id').value);
	    			   }
	    		   },
	    		   'erpLabelPreviewButton':{//模板预览维护，修改样式，不具有增减打印项的
	    		   	  afterrender: function(btn){
	    		   	  	 var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'ENTERING'){
								btn.hide();
							} 
	    		   	  },
	    		   	  click:function(btn){
	    		   	  		//检测是否安装了插件，没有提示安装  	    	  		
    	  		           var LODOP  = getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM'));   	  		           
    	  		           //获取模板参数
    	  		           Ext.Ajax.request({
								url : basePath + "scm/reserve/getdetail.action",
								params : {									
									condition:'la_id='+Ext.getCmp("la_id").value,						
									caller:caller
								},
								method : 'post',
								callback : function(options, success, response) {
									var res = new Ext.decode(response.responseText);
									if (res.exceptionInfo != null) {
										showError(res.exceptionInfo);
										return null;
									} else if(res.data != null){
										 //LODOP.PRINT_INIT("标签模板维护");
										 var strs= new Array(); //定义一数组 
							             strs = res.data[0]['LA_PAGESIZE'].split("*"); //字符分割 	
										 LODOP.SET_PRINT_PAGESIZE(1, strs[0]*10, strs[1]*10, "");
										 Ext.each(res.data,function(data,index){	
										 	LODOP.SET_PRINT_STYLE("FontSize",data['LP_SIZE']);									 	
									       if(data['LP_VALUETYPE'] == 'barcode'){	
										     LODOP.ADD_PRINT_BARCODE(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm",data['LP_ENCODE'],data['LP_VALUETYPE']+data['LP_ID']);
										     LODOP.SET_PRINT_STYLEA(0,"ShowBarText",data['LP_IFSHOWNOTE']);						    			  	
										     LODOP.SET_PRINT_STYLEA(0,"AlignJustify",data['LP_NOTEALIGNJUSTIFY']);
										   }else if(data['LP_VALUETYPE'] == 'text'){
										   	 LODOP.SET_PRINT_STYLE("FontName",data['LP_FONT']);
										     LODOP.ADD_PRINT_TEXT(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm",data['LP_VALUETYPE']+data['LP_ID']);						    			  	
									       }									    
									    });	
									   LODOP.SET_SHOW_MODE("HIDE_ABUTTIN_SETUP",1);
                                       LODOP.SET_SHOW_MODE("HIDE_RBUTTIN_SETUP",1);
									   LODOP.PRINT_SETUP(); 
									   var params = new Object();
	                                   var gridData = new Array();
                                       Ext.each(res.data,function(data,index){
                                       	var o = new Object();
                                       	o['lp_id'] = data['LP_ID'];
                                       	o['lp_laid'] = data['LP_LAID'];
                                       	o['lp_leftrate'] = LODOP.GET_VALUE('ItemLeft',index+1).replace(/"/g, "").replace("mm", "");
                                       	o['lp_toprate'] = LODOP.GET_VALUE('ItemTop',index+1).replace(/"/g, "").replace("mm", "");                                    	
                                       	o['lp_size'] = LODOP.GET_VALUE('ItemFontSize',index+1);
                                       	o['lp_width'] = LODOP.GET_VALUE('ItemWidth',index+1).replace(/"/g, "").replace("mm", "");
                                       	o['lp_height'] = LODOP.GET_VALUE('ItemHeight',index+1).replace(/"/g, "").replace("mm", "");
                                       	if(LODOP.GET_VALUE('ItemContent',index+1) == 'barcode'+data['LP_ID']){                              		                                    		
                                       		o['lp_ifshownote'] = LODOP.GET_VALUE('ItemShowBarText',index+1);                                      		
                                       		o['lp_notealignjustify'] = LODOP.GET_VALUE('ItemAlignJustify',index+1);
                                       		o['lp_encode'] = LODOP.GET_VALUE('ItemFontName',index+1);
                                       		//ItemAlignJustify -该打印项文本两端是否靠齐
                                       	}else if(LODOP.GET_VALUE('ItemContent',index+1) == 'text'+data['LP_ID']){
                                       		o['lp_font'] = LODOP.GET_VALUE('ItemFontName',index+1);
                                       	}
                                       	
                                       	gridData.push(o);
                                       });                         
                                       params.gridStore = unescape(Ext.JSON.encode(gridData).replace(/\\/g,"%"));
                                         Ext.Ajax.request({
									       url : basePath + "scm/reserve/updateLabelT.action",			
									       params: params,			
									       method : 'post',
									       callback : function(options,success,response){
										   var res = new Ext.decode(response.responseText);
										   if(res.exceptionInfo != null){
											  showError(res.exceptionInfo);return;
										   }else {											  
										     //更新参数，刷新界面避免缓存造成预览重叠
										   	 formCondition = getUrlParam('formCondition');
											 gridCondition = getUrlParam('gridCondition');
										   	 formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
										   	 gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
	    								     window.location.href = basePath +'jsps/scm/reserve/setLabelTemplate.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+gridCondition;	    						                                   	
											}
										 }
								      });
									}
								}
							});    	  		          
	    		   	  }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
   				this.GridUtil.onGridItemClick(selModel, record);
	       }
});