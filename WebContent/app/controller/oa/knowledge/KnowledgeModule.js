Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeModule', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.knowledge.KnowledgeModule','oa.knowledge.KnowledgeGrid','core.button.Save'
    	],
    init:function(){
    	this.control({ 
    		'button[id=add]':{
    		  click:function(btn){
			  	var win = new Ext.window.Window({
			    	id : 'win',
   				    height: '300',
   				    width: '450',
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',
   				    	  layout : 'fit',
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeModule' 
   				    	  +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				         
    	     });
    	    win.show();	
           }
         },
         'button[id=sort]':{
         click:function(){
         var win = new Ext.window.Window({
			    	id : 'wingrid',
   				    height: '300',
   				    width: '450',
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',
   				    	  layout : 'fit',
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=KnowledgeModule!Sort&condition=1=1' 
   				    	  +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				    buttons : [{
   				    	text : $I18N.common.button.erpConfirmButton,
   				    	iconCls: 'x-button-icon-confirm',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		var grid = Ext.getCmp('wingrid').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
   				    		var data = grid.getEffectData();                      
		                if(data != null){
			               grid.setLoading(true);
			               Ext.Ajax.request({
		   		           url : basePath + 'oa/knowledge/VastSaveModuleDetno.action',
		   		           params: {
		   			            caller: caller,
		   			            data: Ext.encode(data)
		   		            },
		   		           method : 'post',
		   		          callback : function(options,success,response){
		   			           grid.setLoading(false);
		   			           var localJson = new Ext.decode(response.responseText);
		   			           if(localJson.exceptionInfo){
		   				       showError(localJson.exceptionInfo);
		   				      return "";
		   			        }
	    			        if(localJson.success){
	    				         if(localJson.log){
	    					    showMessage("提示", localJson.log);
	    				     }
		   				     Ext.Msg.alert("提示", "处理成功!", function(){
		   					     win.close();
		   					   var detailgrid= Ext.getCmp('wingrid');		   					   
		   					    gridParam = {caller: 'SaleForecast', condition: condition};
		   					   me.GridUtil.getGridColumnsAndStore(detailgrid, 'common/singleGridPanel.action', gridParam, "")
		   				});
		   			}
		   		}
			});
		   }
   				    	}
   				    }, {
   				    	text : $I18N.common.button.erpCloseButton,
   				    	iconCls: 'x-button-icon-close',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		Ext.getCmp('wingrid').close();
   				    	}
   				    }]
   				         
    	     });
    	    win.show();	
         }       
         },
          'button[id=delete]':{
            click:function(btn){
    	var grid = Ext.getCmp('knowledgeGridPanel');
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length > 0){
			var params = new Object();
			params.caller = caller;
			var data = new Array();
			var bool = false;
			Ext.each(records, function(record, index){
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) ){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
					}
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								o[f] = v;
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool){
				params.data = Ext.encode(data);
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath +'oa/knowledge/VastDeleteKnowledgeModule.action',
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   				return "";
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "删除成功!", function(){
			   					grid.multiselected = new Array();
			   					 var gridParam = {caller:  caller, condition: condition};
                   grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
			   				});
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}

            }          
          }
    	});
    }

});