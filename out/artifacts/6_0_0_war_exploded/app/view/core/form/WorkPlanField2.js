/**
* 计划条目
*/
Ext.define('erp.view.core.form.WorkPlanField2', {
	extend: 'Ext.form.FieldSet',
	id: 'wpfield2',
    alias: 'widget.workplanfield2',
    autoScroll:true,
    minHeight: 220,
    collapsible: true,
    title: '',
    style: 'background:#f1f1f1;',
    margin: '2 2 2 2',
    tfnumber: 0,
    initComponent: function() {
    	console.log(this);
    	this.columnWidth = 1;//强制占一行
    	this.cls = '';
    	this.callParent(arguments);
    	this.items.items[0].name = this.name;
    	this.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+this.fieldLabel);
    },
    layout:'column',
    items: [{
    	xtype: 'form',
    	columnWidth: 1,
    	frame: false,
    	autoScroll:true,
//    	minHeight: 220,
    	bodyStyle: 'background:#f1f1f1;',
    	bbar:[ {
 	    	iconCls: 'x-button-icon-add',
 	    	id: 'add',
 			text: '添加记录',
 			handler: function(btn){
 				btn.ownerCt.ownerCt.ownerCt.addItem(Ext.create('Ext.form.field.Text', {
					xtype: 'textfield',
					name: 'wplan' + ++btn.ownerCt.ownerCt.ownerCt.tfnumber,
					id: 'wplan' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					columnWidth: 0.88,
					value: '',
					fieldLabel: '第&nbsp;' + btn.ownerCt.ownerCt.ownerCt.tfnumber +'&nbsp;条',
					labelWidth: 50,
					fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
					listeners:{
						change: function(){
					    	var s = '';
							for(var i=1; i<=btn.ownerCt.ownerCt.ownerCt.tfnumber; i++){
								if(Ext.getCmp('wplan'+i).value != null && Ext.getCmp('wplan'+i).value.toString().trim() != ''){
									s += Ext.getCmp('wplan'+i).value + '==###==';
								}
							}
							btn.ownerCt.ownerCt.ownerCt.value = s;
						}
					}
				}));
 				btn.ownerCt.ownerCt.ownerCt.addItem(Ext.create('Ext.button.Button', {
 					text: '加入任务',
 					name: 'addtask' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					id: 'addtask' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					columnWidth: 0.07,
					taskid: 0,
					index: btn.ownerCt.ownerCt.ownerCt.tfnumber,
					listeners:{
						afterrender: function(btn){
							if(btn.taskid != 0){
								btn.setText('修改任务');								
							}
						},
						click: function(btn){
							if(Ext.getCmp('wplan'+btn.index).value != null || Ext.getCmp('wplan'+btn.index).value != ''){
								if(btn.taskid == 0){
									btn.taskid = btn.ownerCt.addTask(btn.index);
									btn.setText('修改任务');
								} else {
									btn.ownerCt.updateTask(btn.index);
								}																
							} else {
								alert('任务不能为空');
							}
						}
					}
 				}));
 				btn.ownerCt.ownerCt.ownerCt.addItem(Ext.create('Ext.button.Button', {
 					text: '清&nbsp;空',
 					name: 'cleanbtn' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					id: 'cleanbtn' + btn.ownerCt.ownerCt.ownerCt.tfnumber,
					columnWidth: 0.05,
					index: btn.ownerCt.ownerCt.ownerCt.tfnumber,
					handler: function(btn){
				        Ext.getCmp('wplan'+btn.index).setValue('');
				        if(Ext.getCmp('addtask' + btn.index).taskid != 0){
				        	me.deleteTask(btn.index);
				        	Ext.getCmp('addtask' + btn.index).taskid = 0;
				        	Ext.getCmp('addtask' + btn.index).setText('加入任务');
				        }
 			    	}
 				}));
 			}
 	    }]
    }],
    addBtn: function(){
    	var me = this;
    	me.addItem(Ext.create('Ext.form.Panel', {
        	columnWidth: 1,
        	frame: false,
        	autoScroll:true,
        	bodyStyle: 'background:#f1f1f1;',
        	bbar:[ {
     	    	iconCls: 'x-button-icon-add',
     	    	id: 'add',
     			text: '添加记录',
     			handler: function(btn){
     				me.addItem(Ext.create('Ext.form.field.Text', {
    					xtype: 'textfield',
    					name: 'wplan' + ++me.tfnumber,
    					id: 'wplan' + me.tfnumber,
    					columnWidth: 0.88,
    					value: '',
    					fieldLabel: '第&nbsp;' + me.tfnumber +'&nbsp;条',
    					labelWidth: 50,
    					fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
    					listeners:{
    						change: function(){
    					    	var s = '';
    							for(var i=1; i<=me.tfnumber; i++){
    								if(Ext.getCmp('wplan'+i).value != null && Ext.getCmp('wplan'+i).value.toString().trim() != ''){
    									s += Ext.getCmp('wplan'+i).value + '==###==';
    								}
    							}
    							me.value = s;
    						}
    					}
    				}));
     				me.addItem(Ext.create('Ext.button.Button', {
     					text: '加入任务',
     					name: 'addtask' + me.tfnumber,
    					id: 'addtask' + me.tfnumber,
    					columnWidth: 0.07,
    					taskid: 0,
    					index: me.tfnumber,
    					listeners:{
    						afterrender: function(btn){
    							if(btn.taskid != 0){
    								btn.setText('修改任务');								
    							}
    						},
    						click: function(btn){
    							if(Ext.getCmp('wplan'+btn.index).value != null || Ext.getCmp('wplan'+btn.index).value != ''){
    								if(btn.taskid == 0){
    									btn.taskid = me.addTask(btn.index);
    									btn.setText('修改任务');
    								} else {
    									me.updateTask(btn.index);
    								}																
    							} else {
    								alert('任务不能为空');
    							}
    						}
    					}
     				}));
     				me.addItem(Ext.create('Ext.button.Button', {
     					text: '清&nbsp;空',
     					name: 'cleanbtn' + me.tfnumber,
    					id: 'cleanbtn' + me.tfnumber,
    					columnWidth: 0.05,
    					index: me.tfnumber,
    					handler: function(btn){
    				        Ext.getCmp('wplan'+btn.index).setValue('');
    				        if(Ext.getCmp('addtask' + btn.index).taskid != 0){
    				        	me.deleteTask(btn.index);
    				        	Ext.getCmp('addtask' + btn.index).taskid = 0;
    				        	Ext.getCmp('addtask' + btn.index).setText('加入任务');
    				        }
     			    	}
     				}));
     			}
     	    }]
        }));
    },
    addRecord: function(value, code){
    	var me = this;
    	me.addItem(Ext.create('Ext.form.field.Text', {
			xtype: 'textfield',
			name: 'wplan' + ++me.tfnumber,
			id: 'wplan' + me.tfnumber,
			columnWidth: 0.88,
			value: value,
			fieldLabel: '第&nbsp;' + me.tfnumber +'&nbsp;条',
			labelWidth: 50,
			fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
			listeners:{
				change: function(){
			    	var s = '';
					for(var i=1; i<=me.tfnumber; i++){
						if(Ext.getCmp('wplan'+i).value != null && Ext.getCmp('wplan'+i).value.toString().trim() != ''){
							s += Ext.getCmp('wplan'+i).value + '==###==';
						}
					}
					me.value = s;
				}
			}
		}));
    	me.addItem(Ext.create('Ext.button.Button', {
			text: '加入任务',
			name: 'addtask' + me.tfnumber,
		id: 'addtask' + me.tfnumber,
		columnWidth: 0.07,
		taskid: code,
		index: me.tfnumber,
		listeners:{
			afterrender: function(btn){
				if(btn.taskid != 0){
					btn.setText('修改任务');								
				}
			},
			click: function(btn){				
				if(Ext.getCmp('wplan'+btn.index).value != null || Ext.getCmp('wplan'+btn.index).value != ''){
					if(btn.taskid == 0){
						btn.taskid = me.addTask(btn.index);
						btn.setText('修改任务');
					} else {
						me.updateTask(btn.index);
					}																
				} else {
					alert('任务不能为空');
				}
			}
		}
		}));
    	me.addItem(Ext.create('Ext.button.Button', {
			text: '清&nbsp;空',
			name: 'cleanbtn' + me.tfnumber,
		id: 'cleanbtn' + me.tfnumber,
		columnWidth: 0.05,
		index: me.tfnumber,
		handler: function(btn){
	        Ext.getCmp('wplan'+btn.index).setValue('');
	        if(Ext.getCmp('addtask' + btn.index).taskid != 0){
	        	me.deleteTask(btn.index);
	        	Ext.getCmp('addtask' + btn.index).taskid = 0;
	        	Ext.getCmp('addtask' + btn.index).setText('加入任务');
	        }
	    }
		}));			
    },
    setValue: function(value){
    	this.value = value;
    },
    listeners : {
    	afterrender: function(f){
			var me = this;
			if(f.value != null && f.value.toString().trim() != ''){
				var text = f.value.split('==###==');
				me.tfnumber = text.length;
				for(var i=1; i<=me.tfnumber; i++){
					me.addItem(Ext.create('Ext.form.field.Text', {
						xtype: 'textfield',
						name: 'wplan' + i,
						id: 'wplan' + i,
						columnWidth: 0.88,
						labelWidth: 50,
						value: text[i-1].split('##===##')[0],
						fieldLabel: '第&nbsp;' + i +'&nbsp;条',
						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
						listeners:{
							change: function(){
						    	var s = '';
								for(var i=1; i<=me.tfnumber; i++){
									if(Ext.getCmp('wplan'+i).value != null && Ext.getCmp('wplan'+i).value.toString().trim() != ''){
										s += Ext.getCmp('wplan'+i).value + '==###==';
									}
								}
								me.value = s;
							}
						}
					}));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '加入任务',
	 					name: 'addtask' + i,
						id: 'addtask' + i,
						index: i,
						taskid: text[i-1].split('##===##')[1],
						columnWidth: 0.07,
						listeners:{
							afterrender: function(btn){
								if(btn.taskid != 0){
									btn.setText('修改任务');								
								}
							},
							click: function(btn){
								if(Ext.getCmp('wplan'+btn.index).value != null || Ext.getCmp('wplan'+btn.index).value != ''){
									if(btn.taskid == 0){
										btn.taskid = btn.ownerCt.addTask(btn.index);
										btn.setText('修改任务');
									} else {
										btn.ownerCt.updateTask(btn.index);
									}																
								} else {
									alert('任务不能为空');
								}							
							}
						}
	 				}));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '清&nbsp;空',
	 					name: 'cleanbtn' + i,
						id: 'cleanbtn' + i,
						index: i,
						columnWidth: 0.05,
						handler: function(btn){
					        Ext.getCmp('wplan'+btn.index).setValue('');
					        if(Ext.getCmp('addtask' + btn.index).taskid != 0){
					        	me.deleteTask(btn.index);
					        	Ext.getCmp('addtask' + btn.index).taskid = 0;
					        	Ext.getCmp('addtask' + btn.index).setText('加入任务');
					        }
	 			    	}
	 				}));
				}
			} else {
				me.tfnumber = 5;
				for(var i=1; i<=me.tfnumber; i++){
					me.addItem(Ext.create('Ext.form.field.Text', {
						xtype: 'textfield',
						name: 'wplan' + i,
						id: 'wplan' + i,
						columnWidth: 0.88,
						labelWidth: 50,
						value: '',
						fieldLabel: '第&nbsp;' + i +'&nbsp;条',
						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
						listeners:{
							change: function(){
						    	var s = '';
								for(var i=1; i<=me.tfnumber; i++){
									if(Ext.getCmp('wplan'+i).value != null && Ext.getCmp('wplan'+i).value.toString().trim() != ''){
										s += Ext.getCmp('wplan'+i).value + '==###==';
									}
								}
								me.value = s;
							}
						}
					}));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '加入任务',
	 					name: 'addtask' + i,
						id: 'addtask' + i,
						index: i,
						taskid: 0,
						columnWidth: 0.07,
						listeners:{
							afterrender: function(btn){
								if(btn.taskid != 0){
									btn.setText('修改任务');								
								}
							},
							click: function(btn){
								if(Ext.getCmp('wplan'+btn.index).value != null || Ext.getCmp('wplan'+btn.index).value != ''){
									if(btn.taskid == 0){
										btn.taskid = btn.ownerCt.addTask(btn.index);
										btn.setText('修改任务');
									} else {
										btn.ownerCt.updateTask(btn.index);
									}																
								} else {
									alert('任务不能为空');
								}								
							}
						}
	 				}));
					me.addItem(Ext.create('Ext.button.Button', {
	 					text: '清&nbsp;空',
	 					name: 'cleanbtn' + i,
						id: 'cleanbtn' + i,
						index: i,
						columnWidth: 0.05,
						handler: function(btn){
					        Ext.getCmp('wplan'+btn.index).setValue('');
					        if(Ext.getCmp('addtask' + btn.index).taskid != 0){
					        	me.deleteTask(btn.index);
					        	Ext.getCmp('addtask' + btn.index).taskid = 0;
					        	Ext.getCmp('addtask' + btn.index).setText('加入任务');
					        }
	 			    	}
	 				}));
				}
			}
		}
    },
	addItem: function(item){
		this.add(item);
	},
	deleteTask: function(index){
		var taskid = Ext.getCmp('addtask' + index).taskid;
		Ext.Ajax.request({
	   		url : basePath + 'plm/task/deleteTask.action',
	   		method : 'post',
	   		params:{
	   			id: taskid
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				alert('删除成功');
	   			}
	   		}
		});
	},
	updateTask: function(index){
		var name = Ext.getCmp('wplan'+index).value;
		var taskid = Ext.getCmp('addtask' + index).taskid;
		Ext.Ajax.request({
	   		url : basePath + 'plm/task/updateTaskName.action',
	   		method : 'post',
	   		params:{
	   			name: name,
	   			id: taskid
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				alert('修改成功');
	   			}
	   		}
		});
	},
	addProjectPlan: function(){
		var id = 0;
		Ext.Ajax.request({
	   		url : basePath + 'plm/projectplan/getProjectPlan.action',
	   		method : 'post',
	   		params:{
	   			code: 'WORKPLAN'
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
    				if(rs.projectplan != null){
    					id = rs.projectplan.prjplan_id;
    				} else {
    					var p = new Object();
    					Ext.Ajax.request({
    				   		url : basePath + 'common/getId.action?seq=PROJECTPLAN_SEQ',
    				   		method : 'get',
    				   		async: false,
    				   		callback : function(options,success,response){
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    			        			showError(rs.exceptionInfo);return;
    			        		}
    			    			if(rs.success){
    				   				p.prjplan_id = rs.id;
    				   			}
    				   		}
    					});
    					p.prjplan_prjname = '工作计划';
    					p.prjplan_startdate = Ext.util.Format.date(new Date(), 'Y-m-d');
    					p.prjplan_enddate = '2060-12-29';
    					p.prjplan_code = 'WORKPLAN';
    					p.prjplan_status = '在进行';
    					p.prjplan_description = 'workplan';
    					p.prjplan_statuscode = 'DOING';
    					p.prjplan_date = Ext.util.Format.date(new Date(), 'Y-m-d');
    					var params = new Object();
    					params.formStore = unescape(Ext.JSON.encode(p).replace(/\\/g,"%"));
    					Ext.Ajax.request({
    				   		url : basePath + 'plm/projectplan/insertProjectPlan.action?caller=ProjectPlan',
    				   		params : params,
    				   		method : 'post',
    				   		async: false,
    				   		callback : function(options,success,response){
//    				   			me.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    			    			if(localJson.success){
    			    				id = p.prjplan_id;    			    				
    				   			} else{
    				   				saveFailure();//@i18n/i18n.js
    				   			}
    				   		}
    					});
    				}
	   			}
	   		}
		});
		return id;
	},
	addParentTask: function(){
		var task = '';
		var prjplanid = this.addProjectPlan();
		Ext.Ajax.request({
	   		url : basePath + 'plm/task/getTask.action',
	   		method : 'post',
	   		params:{
	   			code: em_code+'_WORKPLAN_'
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
    				if(rs.task != null){
    					task = rs.task.id + "=" + rs.task.taskcode;
    				} else {
    					var t = new Object();
    					Ext.Ajax.request({
    				   		url : basePath + 'common/getId.action?seq=PROJECTTASK_SEQ',
    				   		method : 'get',
    				   		async: false,
    				   		callback : function(options,success,response){
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    			        			showError(rs.exceptionInfo);return;
    			        		}
    			    			if(rs.success){
    				   				t.id = rs.id;
    				   			}
    				   		}
    					});
    					t.name = em_name +'_工作计划';
    					t.taskcode = em_code + '_WORKPLAN_' + prjplanid;
    					t.prjplanid = prjplanid;
    					t.recorddate = Ext.util.Format.date(new Date(), 'Y-m-d');
    					t.startdate = Ext.util.Format.date(new Date(), 'Y-m-d');
    					t.enddate = '2060-12-29';
    					t.baselinestartdate = '1970-01-01';
    					t.baselineenddate = '2060-12-29';
    					t.taskcolor = 'FF9900';
    					t.recorder = em_name;
    					t.description = 'workplan';
    					t.prjplanname = '工作计划';
    					t.parentid = 0;
    					var params = new Object();
    					params.formStore = unescape(Ext.JSON.encode(t).replace(/\\/g,"%"));
    					Ext.Ajax.request({
    				   		url : basePath + 'plm/task/insertTask.action?caller=ProjectTask',
    				   		params : params,
    				   		method : 'post',
    				   		async: false,
    				   		callback : function(options,success,response){
//    				   			me.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    			    			if(localJson.success){
    			    				task = t.id + '=' + t.taskcode;    			    				
    				   			} else{
    				   				saveFailure();//@i18n/i18n.js
    				   			}
    				   		}
    					});
    				}
	   			}
	   		}
		});
		return task;
	},
	addTeam: function(){
		var prjplanid = this.addProjectPlan();
		var team = new Object();
		Ext.Ajax.request({
	   		url : basePath + 'plm/team/getTeam.action',
	   		method : 'post',
	   		params:{
	   			code: 'WORKPLAN_TEAM'
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
    				if(rs.team != null){
    					team = rs.team;
    				} else {
    					var t = new Object();
    					Ext.Ajax.request({
    				   		url : basePath + 'common/getId.action?seq=TEAM_SEQ',
    				   		method : 'get',
    				   		async: false,
    				   		callback : function(options,success,response){
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    			        			showError(rs.exceptionInfo);return;
    			        		}
    			    			if(rs.success){
    				   				t.team_id = rs.id;
    				   			}
    				   		}
    					});
    					t.team_name = '工作计划';
    					t.team_code = 'WORKPLAN_TEAM';
    					t.team_prjid = prjplanid;
    					t.team_recorddate = Ext.util.Format.date(new Date(), 'Y-m-d');
    					t.team_mothergroupid = 0;
    					var params = new Object();
    					params.formStore = unescape(Ext.JSON.encode(t).replace(/\\/g,"%"));
    					Ext.Ajax.request({
    				   		url : basePath + 'plm/team/insertTeam.action?caller=Team',
    				   		params : params,
    				   		method : 'post',
    				   		async: false,
    				   		callback : function(options,success,response){
//    				   			me.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    			    			if(localJson.success){
    			    				team = t;    			    				
    				   			} else{
    				   				saveFailure();//@i18n/i18n.js
    				   			}
    				   		}
    					});
    				}
	   			}
	   		}
		});
		return team;
	},
	addTeamMember: function(){
		var teammember = new Object();
		var team = this.addTeam();
		Ext.Ajax.request({
	   		url : basePath + 'plm/teammember/getTeamMember.action',
	   		method : 'post',
	   		params:{
	   			employee_code: em_code,
	   			team_id: team.team_id
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
    				if(rs.teammember != null){
    					teammember = rs.teammember;
    				} else {
    					var t = new Object();
    					Ext.Ajax.request({
    				   		url : basePath + 'hr/employee/getEmployee.action',
    				   		method : 'post',
    				   		params:{
    				   			condition: 'em_id=' + em_uu
    				   		},
    				   		async: false,
    				   		callback : function(options,success,response){
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    			        			showError(rs.exceptionInfo);return;
    			        		}
    			    			if(rs.success){
    				   				t.tm_employeejob = rs.employee.em_defaultjbid;
    				   			}
    				   		}
    					});
    					Ext.Ajax.request({
    				   		url : basePath + 'common/getId.action?seq=TEAMMEMBER_SEQ',
    				   		method : 'get',
    				   		async: false,
    				   		callback : function(options,success,response){
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    			        			showError(rs.exceptionInfo);return;
    			        		}
    			    			if(rs.success){
    				   				t.tm_id = rs.id;
    				   			}
    				   		}
    					});
    					t.tm_employeeid = em_uu;
    					t.tm_name = team.team_name;
    					t.tm_prjid = team.team_prjid;
    					t.tm_detno = em_uu;
    					t.tm_employeecode = em_code;
    					t.tm_employeename = em_name;
    					t.tm_teamid = team.team_id;
    					var params = new Object();
    					params.formStore = unescape(Ext.JSON.encode(t).replace(/\\/g,"%"));
    					Ext.Ajax.request({
    				   		url : basePath + 'plm/teammember/saveTeamMember.action?caller=TeamMember',
    				   		params : params,
    				   		method : 'post',
    				   		async: false,
    				   		callback : function(options,success,response){
//    				   			me.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    			    			if(localJson.success){
    			    				teammember = t;    			    				
    				   			} else{
    				   				saveFailure();//@i18n/i18n.js
    				   			}
    				   		}
    					});
    				}
	   			}
	   		}
		});
		return teammember;
	},
	addTask: function(index){
		var code = 0;
		var teammember = this.addTeamMember();
		var parenttask = this.addParentTask();
		var time = Ext.ComponentQuery.query('plandate')[0].items.items[2].value.split(' ~ ');
		var name = Ext.getCmp('wplan'+index).value;
		var o = new Object();
		Ext.Ajax.request({
	   		url : basePath + 'common/getCodeString.action',
	   		method : 'post',
	   		params:{
	   			table: 'PROJECTTASK',
	   			type: 2
	   		},
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				o.taskcode = rs.code;
	   			}
	   		}
		});
		Ext.Ajax.request({
	   		url : basePath + 'common/getId.action?seq=PROJECTTASK_SEQ',
	   		method : 'get',
	   		async: false,
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
    			if(rs.success){
	   				o.id = rs.id;
	   			}
	   		}
		});
		o.name = name;
		o.prjplanid = parenttask.split('_')[2];
		o.prjplanname = '工作计划';
		o.startdate = time[0];
		o.enddate = time[1];
		o.percentdone = 0;
		o.prjtestmancode = '';
		o.parentid = parenttask.split('=')[0];
		o.baselinestartdate = time[0];
		o.baselineenddate = time[1];
		o.taskcolor = 'FF9900';
		o.recorder = em_name;
		o.recorddate = Ext.util.Format.date(new Date(), 'Y-m-d');
		o.isneedattach = 0;
		o.milestone = 0;
		o.description = 'workplan';
		o.point = 0;
		o.files = '';
		var params = new Object();
		params.formStore = unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
		var r = new Object();
		r.ra_id = 0;
		r.ra_detno = 1;
		r.ra_taskid = o.id;
		r.ra_taskname = name;
		r.ra_resourceid = teammember.tm_id;
		r.ra_resourcecode = em_code;
		r.ra_resourcetype = '';
		r.ra_resourcename = em_name;
		r.ra_units = 100;
		r.ra_startdate = time[0];
		r.ra_enddate = time[1];
		r.ra_emid = em_uu;
		r.ra_prjid = parenttask.split('_')[2];
		r.ra_prjname = '工作计划';
		r.ra_taskpercentdone = 0;
		r.ra_type = 0;
		r.ra_needattach = 0;
		params.param = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));	
    	Ext.Ajax.request({
	   		url : basePath + 'plm/task/saveTask.action?caller=ProjectTask',
	   		params : params,
	   		method : 'post',
	   		async: false,
	   		callback : function(options,success,response){
//	   			me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				code = o.id;
    				alert('添加任务成功');
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
		});
    	return code;
	}
});