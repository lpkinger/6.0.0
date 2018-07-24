Ext.define('erp.view.oa.persontask.workPlan.WorkPlanForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpWorkPlanFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
    region: 'center',
    frame : true,
    autoScroll:true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'column',
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'textfield',
		columnWidth: 1,
        fieldLabel: '标题',
        allowBlank: false,
		fieldStyle:"background:#fffac0;color:#515151;",
        id:'wp_title',
        name: 'wp_title',        
        readOnly: true,
        listeners: {
	    	change: function(field){
	    		var me = Ext.getCmp('form');
	    		if(field.value && !getUrlParam('nextworkplan')){//nextworkplan为空--编辑界面
	    			var mm = me.down('workplanfield');
	    			mm.removeAll(true);
	    			var lplan = new Object();
	    			Ext.Ajax.request({//查询当月计划是否存在
	    				url : basePath + 'oa/persontask/workPlan/queryWorkPlan.action',
	    				method : 'post',
	    				params:{
	    					title: Ext.getCmp('lasttitle').value
	    				},
	    				async: false,
	    				callback : function(options,success,response){
	    					var rs = new Ext.decode(response.responseText);
	    					if(rs.exceptionInfo){
	    						showError(rs.exceptionInfo);return;
	    					}
	    					if(rs.success){
	    						lplan = rs.workplan;
	    						if(rs.workplan){//当月计划存在且不为空，则列出当月计划条目
	    							Ext.getCmp('save').last = rs.workplan.wp_id;
	    							Ext.Ajax.request({
	    						   		url : basePath + 'oa/persontask/workPlan/getWorkPlanDetail.action',
	    						   		params : {
	    						   			id: rs.workplan.wp_id
	    						   		},
	    						   		method : 'post',
	    						   		async: false,
	    						   		callback : function(options,success,response){
//	    						   			me.getActiveTab().setLoading(false);
	    						   			var res = new Ext.decode(response.responseText);
	    					    			if(res.success && res.workplandetaillist.length>0){//当月计划条目存在且不为空
	    					    				var values = '';
	    					    				for(var i=0; i<res.workplandetaillist.length; i++){
	    					    					if(i==res.workplandetaillist.length-1){
	    					    						values += res.workplandetaillist[i].wpd_plan;	    						
	    					    					} else {
	    					    						values += res.workplandetaillist[i].wpd_plan + '==###==';	 
	    					    					}
	    					    					
	    					    				}
	    					    				var text = values.split('==###==');
	    					    				mm.tfnumber = text.length;
	    					    				mm.setTitle(mm.title+'('+mm.tfnumber+')');
	    					    				for(var i=1; i<=mm.tfnumber; i++){
	    					    					mm.addItem(Ext.create('Ext.form.field.Text', {
	    					    						xtype: 'textfield',
	    					    						name: 'text' + i,
//	    					    						id: 'text' + i,
	    					    						columnWidth: 1,
	    					    						labelWidth: 30,
	    					    						readOnly: true,
	    					    						value: text[i-1],
	    					    						fieldLabel: i +'&nbsp;',
	    					    						fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
	    					    					}));
	    					    				}
	    						   			} else{
	    						   				saveFailure();//@i18n/i18n.js
	    						   			}
	    						   		}		   		
	    							});
	    						} else {
	    							mm.addItem(Ext.create('Ext.form.field.TextArea', {
	    								xtype: 'textareafield',
	    								name: 'empty',
//	    								id: 'empty',
	    								columnWidth: 1,
	    								height: 30,
	    								frame: false,
	    								readOnly: true,
	    								value: '无数据',
	    								fieldLabel: '',
	    								listeners:{	    									
	    								}
	    							}));
	    						}
	    					}
	    				}
	    			});
	    			Ext.Ajax.request({
	    				url : basePath + 'oa/persontask/workPlan/queryWorkPlan.action',
	    				method : 'post',
	    				params:{
	    					title: field.value
	    				},
	    				async: false,
	    				callback : function(options,success,response){
	    					var rs = new Ext.decode(response.responseText);
	    					if(rs.exceptionInfo){
	    						showError(rs.exceptionInfo);return;
	    					}
	    					if(rs.success){
	    						if(rs.workplan){
	    							Ext.getCmp('wp_summary').setDisabled(true);
	    							Ext.getCmp('nextplan').setDisabled(true);
	    							var url = 'nextworkplan=wp_idIS'+rs.workplan.wp_id;
	    							if(lplan){
		    							url += '&lastworkplan=wp_idIS'+lplan.wp_id;
		    						}
	    							warnMsg(field.value + ' 已存在!是否查看', function(btn){
	    								if(btn == 'yes'){
	    							    	var id = rs.workplan.wp_id;
	    							    	var panel = Ext.getCmp("plan" + id); 
	    							    	var main = parent.Ext.getCmp("content-panel");
	    							    	if(!panel){ 
	    							    		var title = "工作计划查看";
	    								    	panel = { 
	    								    			title : title,
	    								    			tag : 'iframe',
	    								    			tabConfig:{tooltip: field.value},
	    								    			frame : true,
	    								    			border : false,
	    								    			layout : 'fit',
	    								    			iconCls : 'x-tree-icon-tab-tab1',
	    								    			html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/persontask/workPlan/register.jsp?" + url + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    								    			closable : true,
	    								    			listeners : {
	    								    				close : function(){
	    								    			    	main.setActiveTab(main.getActiveTab().id); 
	    								    				}
	    								    			} 
	    								    	};
	    								    	me.FormUtil.openTab(panel, "plan" + id); 
	    							    	}else{ 
	    								    	main.setActiveTab(panel); 
	    							    	} 
	    								} else {
	    									return;
	    								}
	    							});
	    						} else {
	    							Ext.getCmp('wp_summary').setDisabled(false);
	    							Ext.getCmp('nextplan').setDisabled(false);
	    							if(lplan){
	    								Ext.getCmp('import').setDisabled(false);
		    							Ext.getCmp('import').index = lplan.wp_id;
		    						} else {
		    							Ext.getCmp('import').setDisabled(true);
		    						}
	    						}
	    					}
	    				}
	    			});	    			
	    		}
	    	}
	    }
    },{
        xtype: 'hidden',
		columnWidth: 1,
        id:'lasttitle',
        name: 'lasttitle'
    },{
        xtype: 'dbfindtrigger',
		columnWidth: 0.25,
		allowBlank: false,
		fieldStyle:"background:#fffac0;color:#515151;",
        fieldLabel: '计划类型',
//        editable: false,
        id:'wp_type',
        name: 'wp_type',
        listeners: {
        	afterrender: function(field){
        		if(getUrlParam('nextworkplan')){
        			field.setDisabled(true);
        		}
        		if(field.value != null && field.value != ''){
	    			Ext.getCmp('wp_time').setDisabled(false);
	    		} else {
	    			Ext.getCmp('wp_time').setDisabled(true);
	    		}	    			
	    	},
	    	change: function(field){
	    		if(field.value != null && field.value != ''){
	    			Ext.getCmp('wp_time').setDisabled(false);
	    		} else {
	    			Ext.getCmp('wp_time').setDisabled(true);
	    		}
	    	}
	    }
    },{
        xtype: 'hidden',
		columnWidth: 0.25,
        fieldLabel: '类型ID',
        id:'wp_typeid',
        name: 'wp_typeid'
    },{
        xtype: 'hidden',
		columnWidth: 0.25,
        fieldLabel: '员工',
        id:'wp_emp',
        value: em_name,
        name: 'wp_emp'
    },{
        xtype: 'hidden',
		columnWidth: 0.25,
        fieldLabel: '员工ID',
        id:'wp_empid',
        value: em_uu,
        name: 'wp_empid'
    },{
        xtype: 'hidden',
		columnWidth: 0.25,
        fieldLabel: 'ID',
        id:'wp_id',
        name: 'wp_id'
    },{
        xtype: 'plandate',
		columnWidth: 0.75,
        fieldLabel: '计划时间',
        id:'wp_time',
        name: 'wp_time',
        listeners: {

	    }
    },{
        xtype: 'textfield',
		columnWidth: 0.25,
        fieldLabel: '状态',
        value: '在录入',
        readOnly: true,
        id:'wp_status',
        name: 'wp_status'
    },{
        xtype: 'textfield',
		columnWidth: 0.375,
		readOnly: true,
        fieldLabel: '提交时间',
        id:'wp_committime',
        name: 'wp_committime'
    },{
        xtype: 'textfield',
		columnWidth: 0.375,
		readOnly: true,
        fieldLabel: '修改时间',
        id:'wp_updatetime',
        name: 'wp_updatetime'
    },{
        xtype: 'workplanfield',
		columnWidth: 1,
        title: '本月计划',
        id:'lastplan',
        name: 'lastplan'
    },{
    	xtype: 'button',
    	columnWidth: 0.3,
    	index:0,
    	text: '导&nbsp;&nbsp;&nbsp;&nbsp;入&nbsp;&nbsp;&nbsp;&nbsp;计&nbsp;&nbsp;&nbsp;&nbsp;划&nbsp;&nbsp;&nbsp;&nbsp;任&nbsp&nbsp;&nbsp;&nbsp;务',
    	name: 'import',
    	id: 'import',
    	listeners: {
        	afterrender: function(btn){
        		btn.setDisabled(true);
        	},
        	click: function(btn){
//        		alert(btn.index);
        		if(btn.index != 0){
        			var id = btn.index;
        	    	var win = new Ext.window.Window({
        				id : 'win',
        				title: "计划详细查看",
        				height: "60%",
        				width: "60%",
        				maximizable : false,
        				buttonAlign : 'left',
        				layout : 'anchor',
        				items: [{
        					tag : 'iframe',
        					frame : true,
        					anchor : '100% 100%',
        					layout : 'fit',
        					html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/persontask/workPlan/plan.jsp?whoami=WorkPlanDetail&urlcondition=wpd_wpid=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
        				}]
        			});
        			win.show();	
        		}
        	}
        }
    },{
        xtype: 'fieldset',
		columnWidth: 1,
		autoScroll:true,
		margin: '2 2 2 2',
		style: 'background:#f1f1f1;',
		title: '<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;本月总结',
        collapsible: true,
        minHeight: 200,
        layout:'column',
        id: 'wp_summary',
        name: 'wp_summary',
        value:'',
        items:[{
            xtype: 'textareafield',
    		columnWidth: 1,
            fieldLabel: '',
            allowBlank: false,
    		fieldStyle:"background:#fffac0;color:#515151;",
            frame: false,
            height: 250,
            listeners:{
				change: function(field){
					this.ownerCt.value = field.value;
					if(field.value!=null && field.value!=''){
						Ext.getCmp('save').setDisabled(false);
					} else {
						Ext.getCmp('save').setDisabled(true);
					}
				}
			}
        }],
        setValue: function(value){
        	this.value = value;
        }
    },{
        xtype: 'mfilefield',
		columnWidth: 1,
        fieldLabel: '总结附件',
        id:'wp_sumattachs',
        name: 'wp_sumattachs'
    },{
        xtype: 'workplanfield2',
		columnWidth: 1,
        fieldLabel: '下月计划',
        id:'nextplan',
        name: 'nextplan'
    },{
        xtype: 'mfilefield',
		columnWidth: 1,
        fieldLabel: '计划附件',
        id:'wp_planattachs',
        name: 'wp_planattachs'
    },{
        xtype: 'hidden',
		columnWidth: 0.25,
        fieldLabel: '状态码',
        value: 'ENTRING',
        readOnly: true,
        id:'wp_statuscode',
        name: 'wp_statuscode'
    }],
    tbar: [{
    	id: 'save',
    	text: '保存',
    	last: 0,
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	listeners:{
			afterrender: function(btn){
				if(getUrlParam('nextworkplan')){
					btn.setText('更新');
				}
			}
		}
    },{
    	id: 'over',
    	text: '结束',
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){
		var nextworkplan = getUrlParam('nextworkplan');
		var lastworkplan = getUrlParam('lastworkplan');
		this.callParent(arguments);
		this.getLast(lastworkplan);
		this.getNext(nextworkplan);
	},
	getNext: function(nextworkplan){
		if(nextworkplan!=null && nextworkplan!=''){
			var id = nextworkplan.split('IS')[1];
			Ext.Ajax.request({
		   		url : basePath + 'oa/persontask/workPlan/getWorkPlan.action',
		   		params : {
		   			id: id
		   		},
		   		method : 'post',
		   		async: false,
		   		callback : function(options,success,response){
//		   			me.getActiveTab().setLoading(false);
		   			var res = new Ext.decode(response.responseText);
	    			if(res.success){
	    				Ext.getCmp('wp_id').setValue(res.workplan.wp_id);
	    				Ext.getCmp('wp_type').setValue(res.workplan.wp_type);
	    				Ext.getCmp('wp_typeid').setValue(res.workplan.wp_typeid);
	    				Ext.getCmp('wp_emp').setValue(res.workplan.wp_emp);
	    				Ext.getCmp('wp_empid').setValue(res.workplan.wp_empid);
	    				Ext.getCmp('wp_summary').setValue(res.workplan.wp_summary.replace(/\%n/g,"\n"));
	    				Ext.getCmp('wp_status').setValue(res.workplan.wp_status);
	    				Ext.getCmp('wp_statuscode').setValue(res.workplan.wp_statuscode);
	    				Ext.getCmp('wp_sumattachs').setValue(res.workplan.wp_sumattachs);
	    				Ext.getCmp('wp_planattachs').setValue(res.workplan.wp_planattachs);
	    				if(res.workplan.wp_committime){
	    					Ext.getCmp('wp_committime').setValue(Ext.util.Format.date(new Date(res.workplan.wp_committime),"Y-m-d H:i:s"));	
	    				}	    				
	    				if(res.workplan.wp_updatetime){
	    					Ext.getCmp('wp_updatetime').setValue(Ext.util.Format.date(new Date(res.workplan.wp_updatetime),"Y-m-d H:i:s"));	    					
	    				}
	    				Ext.getCmp('wp_time').setValue(res.workplan.wp_time);
	    				Ext.getCmp('wp_title').setValue(res.workplan.wp_title);
	    				var values = '';
	    				for(var i=0; i<res.workplandetaillist.length; i++){
	    					if(i==res.workplandetaillist.length-1){
	    						values += res.workplandetaillist[i].wpd_plan + '##===##' + res.workplandetaillist[i].wpd_taskid;	    						
	    					} else {
	    						values += res.workplandetaillist[i].wpd_plan + '##===##' + res.workplandetaillist[i].wpd_taskid + '==###==';	 
	    					}	    					
	    				}
	    				Ext.getCmp('nextplan').setValue(values);
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}		   		
			});
		}
	},
	getLast: function(lastworkplan){
		if(lastworkplan!=null && lastworkplan!=''){
			var id = lastworkplan.split('IS')[1];
			Ext.Ajax.request({
		   		url : basePath + 'oa/persontask/workPlan/getWorkPlanDetail.action',
		   		params : {
		   			id: id
		   		},
		   		method : 'post',
		   		async: false,
		   		callback : function(options,success,response){
//		   			me.getActiveTab().setLoading(false);
		   			var res = new Ext.decode(response.responseText);
	    			if(res.success){
	    				var values = '';
	    				for(var i=0; i<res.workplandetaillist.length; i++){
	    					if(i==res.workplandetaillist.length-1){
	    						values += res.workplandetaillist[i].wpd_plan;	    						
	    					} else {
	    						values += res.workplandetaillist[i].wpd_plan + '==###==';	 
	    					}
	    					
	    				}
	    				Ext.getCmp('lastplan').setValue(values);
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}		   		
			});
		}
	}
});