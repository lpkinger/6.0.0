/**
 * dbfind trigger
 * 支持带条件dbfind  --之后往后添加
 */
Ext.define('erp.view.core.trigger.AddDbfindTrigger', {
    				   extend: 'Ext.form.field.Trigger',
    				   alias: 'widget.adddbfindtrigger',
    				   triggerCls: 'x-form-search-trigger',
    				   initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true,
    							beforetrigger: true
    					   });
    					   this.callParent(arguments);  
    				   },
    				   listeners: {//功能：自动取数据(当前dbfind结果只有一条数据时，直接赋值，无需弹出window)
    					   focus: function(f){
    						   if(!f.readOnly) {
    							   var trigger = this;
        						   trigger.lastTriggerId = trigger.id;
        						   if(!trigger.ownerCt){
        							   if(!trigger.owner){
        								   var grid = Ext.ComponentQuery.query('gridpanel');
    	   		    				   	   Ext.Array.each(grid, function(g, index){
    	   		    				   	    	Ext.Array.each(g.columns, function(column){
    	   		            				   		if(column.dataIndex == trigger.name) {
    	   		            				   			dbfind = column.dbfind;
    	   		            				   			trigger.owner = g;
    	   		            				   		}
    	   		            				   	});
    	   		    				   	   });
        							   }
        							   if(trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
    									   trigger.record = trigger.owner.editingPlugin.activeRecord;
    								   } else {
    									   trigger.record = trigger.owner.selModel.lastSelected;
    								   }
        							   var index = trigger.record.index;
    								   if(index != null){
    									   trigger.lastTriggerId = trigger.id + '---' + index;
    								   } else {
    									   trigger.lastTriggerId = null;
    								   }
        						   }
    						   }
    					   }
    				   },
     				   onTriggerClick: function() {
     					   	var trigger = this, bool = true;// 放大镜所在	
     					   	bool = trigger.fireEvent('beforetrigger', trigger);
     					   	if(bool == false) {
     					   		return;
     					   	}
     					    this.setFieldStyle('background:#C6E2FF;');
     					    
     					   	var key = this.name,// name属性
    				        dbfind = '',// 需要dbfind的表和字段
						    dbBaseCondition='',
                            dbCondition='',
                            dbGridCondition='',
                            findConfig=this.findConfig,
							dbKey=this.dbKey,
							mappingKey=this.mappingKey,
							gridKey=this.gridKey,
							dbCaller=this.dbCaller|| (typeof caller === 'undefined' ? '' : caller)
							mappinggirdKey=this.mappinggirdKey;
    				   		window.onTriggerClick = this.id;
							// 存在查询条件的字段
    				   		if(findConfig){
    				   			dbCondition = findConfig;
    				   		}
							if(dbKey){
								var dbKeyValue = Ext.getCmp(dbKey).value;
								if(dbKeyValue){
									dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
								} else {
									showError(this.dbMessage);
									return
								}
							}
							if(gridKey){
								
								var gridKeys = gridKey.split('|');
								var mappinggirdKeys = mappinggirdKey.split('|');
								var gridErrorMessages = this.gridErrorMessage.split('|');
								
								for(var i=0;i<gridKeys.length;i++){
									var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
									
									if(i==0){
										if(gridkeyvalue){
											dbGridCondition = mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
										}else{
											showError(gridErrorMessages[i]);
									    	return
										}
									}else{
										if(gridkeyvalue){
											dbGridCondition =dbGridCondition+" AND "+ mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
										}else{
											showError(gridErrorMessages[i]);
									    	return
										}
										
									}
									
								}

							}
							if(this.dbBaseCondition){
								dbBaseCondition = this.dbBaseCondition;
							}
    				   	if(!trigger.ownerCt){// 如果是grid的dbfind
    				   	    var grid = Ext.ComponentQuery.query('gridpanel');
    				   	    Ext.Array.each(grid, function(g, index){
    				   	    	Ext.Array.each(g.columns,function(column){
            				   		if(column.dataIndex == key) {
            				   			dbfind = column.dbfind;
            				   			trigger.owner = g;
            				   		}
            				   	});
    				   	    });
        				   	
    				   	}
    				   	var keyValue = this.value;// 当前值
    				   	keyValue = keyValue == null ? '' : keyValue;
    				   	var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
    				   			height = Ext.isIE ? screen.height*0.75 : '95%';
    				   	var _config=getUrlParam('_config');
	   			    	var dbwin = new Ext.window.Window({
	   			    		id : 'dbwin',
		   				    title: '查找',
		   				    height: height,
		   				    width: width,
		   				    maximizable : true,
		   					buttonAlign : 'center',
		   					layout : 'anchor',
		   				    items: [{
		   				    	  tag : 'iframe',
		   				    	  frame : true,
		   				    	  anchor : '100% 100%',
		   				    	  layout : 'fit',
		   				    	  html : '<iframe id="iframe_dbfind" src="'+basePath+'jsps/common/dbfind.jsp?dbkind=add&key='+key+"&dbfind="+dbfind+"&dbGridCondition="+dbGridCondition+"&dbCondition="+dbCondition+"&dbBaseCondition="+dbBaseCondition+"&keyValue="+encodeURIComponent(keyValue)+"&trigger="+trigger.id+ "&caller=" + dbCaller +"&_config="+_config+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		   				    }],
		   				    buttons : [{
		   				    	text : '关  闭',
		   				    	iconCls: 'x-button-icon-close',
		   				    	cls: 'x-btn-gray',
		   				    	handler : function(){
		   				    		Ext.getCmp('dbwin').close();
		   				    	}
		   				    },{
		   				    	text: '重置条件',
		   				    	id: 'reset',
		   				    	cls: 'x-btn-gray',
		   				    	hidden: true,
		   				    	handler: function(){
		   				    		var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
		   				    		grid.resetCondition();
		   				    		grid.getCount();
		   				    	}
		   				    }]
		   				});
		   				dbwin.show();
		   				trigger.lastTriggerId = null;
    				   }
    				  
    				});