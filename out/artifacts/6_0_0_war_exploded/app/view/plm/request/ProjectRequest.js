Ext.define('erp.view.plm.request.ProjectRequest',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		formCondition = getUrlParam('formCondition');
		gridCondition = getUrlParam('gridCondition');
		if(formCondition!=''&&(!gridCondition||gridCondition=='')&&formCondition!=null){
			me.getUrl(formCondition);
		}else{	
			gridCondition==null?'':gridCondition;
			formCondition==null?'':formCondition;
			var src;
			if(!formCondition){
				src = '<iframe id="gant" src="'+basePath+'jsps/plm/task/gantt.jsp?formCondition=prjplanidIS-1&readOnly=0&hideToolBar=true" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>';
			}else{
				src = '<iframe id="gant" src="'+basePath+'jsps/plm/task/gantt.jsp?formCondition='+formCondition.replace(/'/g,'')+'&readOnly=0&hideToolBar=true" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>';
			}
        	var boolProjectSob = me.getBoolProjectSob();
        	var boolprjGant = caller=='ProjectRequest'?(me.getBoolProject()):false;
			Ext.apply(me, { 
				items: [{
						xtype: 'erpFormPanel',
						anchor: '100% 40%',
						saveUrl: 'plm/request/saveProjectRequest.action',
						deleteUrl: 'plm/request/deleteProjectRequest.action',
						updateUrl: 'plm/request/updateProjectRequest.action',
						auditUrl: 'plm/request/auditProjectRequest.action',
						resAuditUrl: 'plm/request/resAuditProjectRequest.action',
						submitUrl: 'plm/request/submitProjectRequest.action',
						resSubmitUrl: 'plm/request/resSubmitProjectRequest.action',
						getIdUrl: 'common/getId.action?seq=PROJECT_SEQ',
						planTaskUrl:'plm/request/planMainTask.action',
						turnProject:'plm/request/turnProjectStatus.action',
						keyField: 'prj_id',
						statusField: 'prj_status'
					},
					{
						xtype:'tabpanel',
						anchor : '100% 60%',
						id : 'myTab',
						items : [
							{					
								xtype: 'ProjectPanel',
								caller:'ProjectRequest',
								id :'grid',
								anchor : '100% 60%',
								title:'项目阶段计划',
								detno: 'pp_detno',
								gridCondition:'',
								necessaryField: '',
								keyField: 'pp_id',
								allowExtraButtons:true,
								mainField: 'pp_prjid',
								plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
									clicksToEdit: 1						
								}), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
							},
							{					
								xtype: 'ProjectPanel',
								caller:'ProjectSob',
								id:'ProjectSob',
								anchor : '100% 60%',
								title:'子项目明细',
								hidden:boolProjectSob,
								keyField: 'prj_id',
								allowExtraButtons:true,
								mainField: 'prj_mainproid',
								plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
									clicksToEdit: 1						
								}), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
							},
							{
								title : '预立项任务',
								tag : 'iframe',
								id:'preTask',
								border : false,
								anchor : '100% 60%',
								hidden:boolprjGant,
								html:boolprjGant?'':src
							},
							{					
								xtype: 'ProjectPanel',
								caller : 'ProjectTeam',
								id:'ProjectTeam',
								anchor : '100% 60%',
								title:'项目团队',
								necessaryField: '',
								keyField: 'tm_id',
								allowExtraButtons:true,
								mainField: 'tm_prjid',
								plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
									clicksToEdit: 1						
								}), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
							}
						]
						}
					]
			}); 
		}
		me.callParent(arguments);
	},
	getUrl:function(formCondition){
		Ext.Ajax.request({
			url : basePath + 'plm/request/getIdByCode.action',
			params: {formCondition:formCondition},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var id = res.id;
					formCondition = 'prj_idIS'+id;
					gridCondition = 'pp_prjidIS'+id;
					window.location.href = window.location.href.substring(0,window.location.href.lastIndexOf('?')) + '?whoami='+caller+'&formCondition=' + 
					formCondition + '&gridCondition=' + gridCondition;
				} else if(res.exceptionInfo){					
					showError(res.exceptionInfo);
				} 
			}
		});
	},
	getBoolProjectSob:function(){
		var bool;
		var id = getUrlParam('formCondition');
		if(null==id||''==id){
			return false;
		}else{
			Ext.Ajax.request({
				   url:basePath+'plm/request/isProjectSobHaveData.action',
				   params:{
					   id:id.replace(/IS/g, "="),caller:caller
				   },
				   async : false,
				   callback:function(options, success, response){
					   var localJson = new Ext.decode(response.responseText);
					   bool =  localJson.result;
				   }
				 });
				return bool;
		}
	},
	getBoolProject:function(){
		var bool;
		var id = getUrlParam('formCondition');
		if(null==id||''==id){
			return false;
		}else{
			Ext.Ajax.request({
				   url:basePath+'plm/request/isProjectTaskHaveData.action',
				   params:{
					   id:id.replace(/IS/g, "="),caller:caller
				   },
				   async : false,
				   callback:function(options, success, response){
					   var localJson = new Ext.decode(response.responseText);
					   bool =  localJson.result;
				   }
				 });
				return !bool;
		}
	}
});