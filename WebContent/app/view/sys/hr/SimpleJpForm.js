Ext.define('erp.view.sys.hr.SimpleJpForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.simplejpform',
	id : 'simplejpform',
	width : '100%',
	height:48,
	labelWidth : 40,
	style:'border-width:0 0 0 0;',
	border:false,
	/*bodyPadding : 5,*/
	bodyStyle: {
	    background: '#f0efed',
	},
	/*bodyBorder:false,*/
	layout : '',
	items : [ {
		autoHeight : true,
		layout : "column",
		baseCls : 'my-panel-no-border',
		items : [ {
			columnWidth : .90,
			xtype : "fieldset",
			/* title: "个人信息", */
			layout : "form",
			/*baseCls : 'my-panel-no-border',*/
			style : "margin-left: 2px;padding:3px;border: 0px solid #b5b8c8",
			fieldDefaults : {
				labelWidth : 70,
				labelAlign : "left"
			},
			items : [ {
				xtype : 'label',
				id: 'jpname',
				style:'',
				labelStyle : 'text-align:left;width:50;',
				name : 'jpname',
				html: '<b>流程名称:</b>',
				width : '100%'
			}, {
				xtype : 'label',
				id: 'jpdescription',
				labelStyle : 'text-align:left;width:50;',
				name : 'jpdescription',
				html: '<b>流程说明:</b>',
				width : '100%'
			},{
				xtype:'textfield',
				name:'jpcaller',
				id:'jpcaller',
				hidden:true
			},{
				xtype:'textfield',
				name:'jpenabled',
				id:'jpenabled',
				hidden:true
			},{
				xtype:'textfield',
				name:'jpressubmit',
				id:'jpressubmit',
				hidden:true
			},{
				xtype:'textfield',
				name:'jpparentid',
				id:'jpparentid',
				hidden:true	
			},{
				xtype:'textfield',
				name:'simplejpid',
				id:'simplejpid',
				hidden:true	
			}/*,{	
				xtype:'checkboxfield',
				fieldLabel : '<b>是否启用</b>',
				labelStyle : 'text-align:left;width:50;',
				name: 'disabled',
				inputValue: '1',
			}*/]
		}, {
			columnWidth : .10,
			xtype : "fieldset",
			layout : "form",
			baseCls : "noboder",
			style : "margin-left: -50px;margin-top: -3px;border: 0px solid #b5b8c8",
			/*fieldDefaults : {
				labelWidth : 70,
				labelAlign : "left"
			},*/
			items : [ {
				xtype : 'button',
				text : '保存',
				id : 'saveProcessBtc',
				iconCls:'savebt',
				style:{background:'#0092d0'},
				height:30,
				width:80,
				handler:function(){
					var agElements = document.getElementsByName("assignInfo");
					var xml="<process xmlns='http://jbpm.org/4.4/jpdl' name='"+Ext.getCmp('jpname').el.dom.innerText.substring(6,Ext.getCmp('jpname').el.dom.innerText.length)+"'>  <start "+
					"g='349,101,48,48' name='start 1'>"+ "<transition name='to task 1' to='task 1'/>"+
						  "</start>";
					if(agElements.length>1){
						for(var x=0;x<(agElements.length-1);x++){
						    if(agElements[x].getAttribute("data-type") && agElements[x+1].getAttribute("data-type") && agElements[x].getAttribute("data-type")!=agElements[x+1].getAttribute("data-type")){
						    	Ext.Msg.alert('提示', '不能同时指定人员，岗位，领导！');
						    	return false;
						    };
						}	
						
					}
					for(var i=0;i<agElements.length;i++){
						if(i==agElements.length-1 && agElements[i].getAttribute("data-type")=="assignee" && agElements[i].getAttribute("data-code")!=""){
							xml=xml+"<task assignee='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
						    +"<transition name='同意' to='end 1'/>"+
						        "<transition name='不同意' to='cancel 1'/>"+
						      "</task>"
						}else if(i==agElements.length-1 && agElements[i].getAttribute("data-type")=="candidate-groups" && agElements[i].getAttribute("candidate-groups")!=""){
							xml=xml+"<task candidate-groups='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
						    +"<transition name='同意' to='end 1'/>"+
						        "<transition name='不同意' to='cancel 1'/>"+
						      "</task>"
						}else if(i==agElements.length-1 && agElements[i].getAttribute("data-type")=="rolAssignee" && agElements[i].getAttribute("rolAssignee")!=""){
							xml=xml+"<task rolAssignee='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
						    +"<transition name='同意' to='end 1'/>"+
						        "<transition name='不同意' to='cancel 1'/>"+
						      "</task>"
						}else{
							if(agElements[i].getAttribute("data-type")=="assignee" && agElements[i].getAttribute("data-code")!=""){
								xml=xml+"<task assignee='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
							    +"<transition name='同意' to='task "+(i+2)+"'/>"+
							        "<transition name='不同意' to='cancel 1'/>"+
							      "</task>"
							}else if(agElements[i].getAttribute("data-type")=="candidate-groups" && agElements[i].getAttribute("data-code")!=""){
								xml=xml+"<task candidate-groups='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
								 +"<transition name='同意' to='task "+(i+2)+"'/>"+
							        "<transition name='不同意' to='cancel 1'/>"+
							      "</task>"
							}else if(agElements[i].getAttribute("data-type")=="rolAssignee" && agElements[i].getAttribute("data-code")!=""){
								xml=xml+"<task rolAssignee='"+agElements[i].getAttribute("data-code")+"' g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
								 +"<transition name='同意' to='task "+(i+2)+"'/>"+
							        "<transition name='不同意' to='cancel 1'/>"+
							      "</task>"
							}else{
								xml=xml+"<task g='329,"+(190+(100*i))+",90,50' name='task "+(i+1)+"'>"
							    +"<transition name='同意' to='task "+(i+2)+"'/>"+
						        "<transition name='不同意' to='cancel 1'/>"+
							      "</task>"
							}
						}
						
					}
					xml=xml+"<cancel g='199,292,48,48' name='cancel 1'/>"+
						  "<end g='349,"+(190+(100*agElements.length))+",48,48' name='end 1'/>"+
						"</process>";
					  Ext.Msg.wait('正在保存...');
	                	 Ext.Ajax.request({
	                    	  method: 'post',
	                          url: basePath+'common/deployProcess.action',
	                          success: function(response) {
	                           var o = Ext.decode(response.responseText);
	                           if(o.exceptionInfo){
	                           Ext.Msg.alert('提示', o.exceptionInfo);
	                           }                                
	                           if (o.success == true) {
	                                  	Ext.Msg.alert('提示', '保存成功, 你的流程定义编号为:'+o.id);
	                               } else {
	                                      Ext.Msg.alert('错误', o.errors.msg);
	                           }
	                          },
	                          failure: function(response) {
	                        	  var o = Ext.decode(response.responseText);
	                        	  Ext.Msg.alert('保存失败','请检查 xml文件格式！'+o.exceptionInfo );
	                          },
	                          params: {
	                              processDefinitionName: Ext.getCmp('jpname').el.dom.innerText.substring(6,Ext.getCmp('jpname').el.dom.innerText.length),
	                              processDescription: Ext.getCmp('jpdescription').el.dom.innerText.substring(6,Ext.getCmp('jpdescription').el.dom.innerText.length),
	                              caller:Ext.getCmp('jpcaller').value,
	                              enabled:Ext.getCmp('jpenabled').value,
	                              ressubmit:Ext.getCmp('jpressubmit').value,
	                              xml: xml,
	                              parentId:Ext.getCmp('jpparentid').value
	                          }
	                      });
				}
			} ]
		}

		]
	} ]

})