/**
 * dbfind trigger
 * 支持带条件dbfind
 */
Ext.define('erp.view.core.trigger.DbfindTrigger2', {
    				   extend: 'Ext.form.field.Trigger',
    				   alias: 'widget.dbfindtrigger2',
    				   triggerCls: 'x-form-search-trigger',
    				   editable: false,
    				   initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true
    					   });
    					   this.callParent(arguments);  
    				   },
     				   onTriggerClick: function() {
     					   this.setFieldStyle('background:#C6E2FF;');
     					   var trigger = this;// 放大镜所在
//     					   	key = this.name,// name属性
//    				        dbfind = '',// 需要dbfind的表和字段
//						    dbBaseCondition='',
//                            dbCondition='',
//                            dbGridCondition='',
//							dbKey=this.dbKey,
//							mappingKey=this.mappingKey,
//							gridKey=this.gridKey,
//							mappinggirdKey=this.mappinggirdKey;
//    				   		window.onTriggerClick = this.id;
//							// 存在查询条件的字段
//							if(dbKey){
//								var dbKeyValue = Ext.getCmp(dbKey).value;
//								if(dbKeyValue){
//									dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
//								} else {
//									showError(this.dbMessage);
//									return
//								}
//							}
//							if(gridKey){
//							    var gridkeyvalue = Ext.getCmp(gridKey).value;
//							    if(gridkeyvalue){
//							    	dbGridCondition = this.mappinggirdKey + " IS '" + gridkeyvalue + "'";
//							    } else {
//							    	showError(this.gridErrorMessage);
//							    	return
//							    }
//							}
//							if(this.dbBaseCondition){
//								dbBaseCondition = this.dbBaseCondition;
//							}
//    				   	if(!trigger.ownerCt){// 如果是grid的dbfind
//    				   	    var grid = Ext.ComponentQuery.query('gridpanel');
//    				   	    Ext.Array.each(grid, function(g, index){
//    				   	    	Ext.Array.each(g.columns,function(column){
//            				   		if(column.dataIndex == key) {
//            				   			dbfind = column.dbfind;
//            				   			trigger.owner = g;
//            				   		}
//            				   	});
//    				   	    });
//        				   	
//    				   	}
//    				   	var keyValue = this.value;// 当前值
//    				   	keyValue = keyValue == null ? '' : keyValue;
	   			    	var dbwin = new Ext.window.Window({
	   			    		id : 'dbwin',
		   				    title: '选择目录',
		   				    height: "100%",
		   				    width: "30%",
		   				    maximizable : true,
		   					buttonAlign : 'center',
		   					layout : 'anchor',
		   				    items: [{
		   				    	  tag : 'iframe',
		   				    	  frame : true,
		   				    	  anchor : '100% 100%',
		   				    	  layout : 'fit',
		   				    	  html : '<iframe id="iframe_dbfind" src="'+basePath+'jsps/oa/document/dbfind2.jsp?trigger=' + trigger.id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		   				    }],
		   				    buttons : [{
		   						iconCls: 'tree-add',
		   						text: '确定',
		   						handler: function(btn){
		   							var tree = btn.ownerCt.ownerCt.items.items[0].el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp("treegrid");
		   							var url = '';
		   							if(tree.expandedNodes.length == 0){
		   								showError('亲，请选择路径');
		   							} else {
		   								Ext.each(tree.expandedNodes, function(){
		   									if(this.data['dc_isfile'] == 'F'){
		   										url += '/' + this.data['qtip'];	    			
		   									}
		   								});
		   								trigger.setValue(tree.expandedNodes.pop().data['dc_id'] + ':' + url);
//		   					    	    trigger.fireEvent('aftertrigger', trigger);
		   								Ext.getCmp('dbwin').close();		   								
		   							}		   							
		   						}
		   					},{
		   				    	text : '关  闭',
		   				    	iconCls: 'x-button-icon-close',
		   				    	cls: 'x-btn-gray',
		   				    	handler : function(){
		   				    		Ext.getCmp('dbwin').close();
		   				    	}
		   				    }]
		   				});
		   				dbwin.show();
							
    				   }
    				});