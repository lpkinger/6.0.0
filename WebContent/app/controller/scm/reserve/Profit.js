Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Profit', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'scm.reserve.profit.Viewport','scm.reserve.profit.GridPanel','core.trigger.AddDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.button.Close',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.button.GenerateBarcode','core.button.PrintAll','core.button.DeleteAllDetails',
     	    'core.button.PrintAllPackage','core.button.GeneratePaCode','core.button.PrintBarcode','core.button.PrintOutboxcode'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	var LODOP = null;
    	this.control({
    		'erpGridPanel2': { 
    			reconfigure:function(grid){
    			},
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=refresh]':{//刷新
    			click: function(btn){
    			  window.location.href = window.location.href;
    			}
    		},
    		'button[id=batchGenBarcode]':{//生成条码
    			click: function (btn){
    				var linkCaller = "profitBarcode";
    				var id =key;
    				var no=inoutno;
    				var pi_class=(formCondition.split('IS')[formCondition.split('IS').length-1]).replace("'","").replace("'","");
                    var formCondition1 = "bs_idIS" + id +" and bs_codeIS'"+no+"'"+"and bs_classIS'"+pi_class+"'";
                    var gridCondition1 = "bsd_bsidIS" + id+"and nvl((select sum(bdd_inqty) from barstocktakingdetaildet where bsd_id=bdd_bsdid),0)<bsd_inqty "+
                    					" group by bsd_bsid,bsd_detno,bsd_id,bsd_prodcode,pr_detail,pr_tracekind,bsd_inqty,pr_zxbzs order by  bsd_detno asc";
                                var win = new Ext.window.Window({
                                    id: 'win',
                                    height: '80%',
                                    width: '90%',
                                    maximizable: true,
                                    title:'<span><font color=blue>条形码维护['+(formCondition.split('IS')[formCondition.split('IS').length-1]).replace("'","").replace("'","")+':'+inoutno+']</font></span>',
                                    buttonAlign: 'center',
                                    layout: 'anchor',
                                    closeAction:'hide',
                                    items: [{
                                        tag: 'iframe',
                                        frame: true,
                                        anchor: '100% 100%',
                                        layout: 'fit',
                                        html: '<iframe id="iframe_' + linkCaller + '" src="' + basePath + 'jsps/scm/reserve/profitBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutno+ '&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1 + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                    }],
                                    listeners:{
                                    	hide:function(){
                                    		window.location.reload();
                                    	}
                                    }
                                });
                                win.show();
                   
                },
                afterrender: function(btn) {
                	if(status == '已审核'){
    					btn.hide();
    				}
                }
    		},    		
    			//删除全部明细
    		'erpDeleteAllDetailsButton':{
    			  afterrender: function(btn) {
                  	if(status == '已审核'){
      					btn.hide();
      				}
                  },
    			click:function (btn){
	    			var grid =Ext.getCmp("profitGridPanel");
	    			var items = grid.store.data.items;
	    			var bool = false;
	    			var array = new Array();
	    			Ext.each(items, function(item, index){
        	         if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		        && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){    
        		        	bool = true;
        		        } 
	    			})
	    			if(!bool){
	    				showError("没有需要处理的数据!");
	    				 return;
	    			  }else{
	    				  var records=grid.getSelectionModel().getSelection();
                          if(records.length<=0){
     	    			  	 warnMsg("确定清空全部条码", function(btn){
     					         if(btn == 'yes'){
     			    			  	Ext.Ajax.request({
     								url : basePath + "scm/reserve/barStock/deleteAllBarDetails.action",			
     								params: {     
     									      caller: caller,
     					 			          id:key
     					 			        },			
     								method : 'post',
     								callback : function(options,success,response){
     									var res = new Ext.decode(response.responseText);
     									if(res.exceptionInfo != null){
     										var str = res.exceptionInfo;
     										if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
     											str = str.replace('AFTERSUCCESS', '');
     											window.location.href = window.location.href;
     										}
     										showError(str);return;
     									}else {
     										window.location.href = window.location.href;
     									}
     								 }
     							    }) 
     					       }else{
     					       	  return ;
     					       }
     			         })
                          }else{
                        	  Ext.each(records, function (item) {
                        		  array.push(item.data['bdd_id']);
                              })
                              var bddids=array;
                          Ext.Ajax.request({
								url : basePath + "scm/reserve/barStock/deleteAllBarDetails.action",			
								params: {     
									      caller: caller,
					 			          id:key,
					 			          bddids:bddids
					 			        },			
								method : 'post',
								callback : function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										var str = res.exceptionInfo;
										if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
											str = str.replace('AFTERSUCCESS', '');
											window.location.href = basePath +'jsps/scm/reserve/profit.jsp?_noc=1&whoami='+caller+'&key='+key+'&inoutno='+inoutno+'&formCondition='+formCondition+'&gridCondition='+gridCondition;
										}
										showError(str);return;
									}else {
										window.location.href = basePath +'jsps/scm/reserve/profit.jsp?_noc=1&whoami='+caller+'&key='+key+'&inoutno='+inoutno+'&formCondition='+formCondition+'&gridCondition='+gridCondition;
									}
								 }
							    }) 
                           
                          }
	    			}
	    		}
    		}
      });
     },
     onGridItemClick: function(selModel, record){// grid行选择
     	this.GridUtil.onGridItemClick(selModel, record);
     },
});
    
