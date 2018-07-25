Ext.define('erp.view.sys.pr.ProductPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.productportal', 
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	layout:'border',
	items : [ {
		region : 'center',
		xtype : 'panel',
		title : '岗位信息',
		width : 400,
		minWidth : 400,
		layout : 'anchor',//'border',
		items : [ {
			// region:'north',
			xtype : 'form',
			anchor : '100% 30%',
			id : 'saasjobdescriptform',
			bodyPadding : 10,
			items : [  
			  {
			    xtype : 'button',
			    iconCls:'btn-savejob',
			    id : 'savejob11button',
			    margin: '0 5 0 0',
			    text : '保存',
			    disabled:true,
			    value : '',
			   handler : function() {
			        console.log('hehe');
			        var jobdescription=Ext.getCmp('titleRemort');
			        var jobpower=Ext.getCmp('saasjobpower');
			        console.log(jobdescription.value);
			        console.log(jobpower.value);
			        Ext.Ajax.request({
						url:basePath+'hr/employee/updateDescrption.action',
						params: {
							"jo_id":this.value,
			        		"jo_power":jobpower.value,
			        		"jo_description":jobdescription.value
						},
						method : 'post',
						callback : function(options,success,response){
							var local=Ext.decode(response.responseText);
							if(local.success) {
								showResult('提示','修改成功!');
							}else {
								showResult('提示',local.exceptionInfo);
							}
						}
					});
			     }
			  },
			{
				xtype : 'button',
				iconCls:'btn-power',
				id : 'myjobbutton',
				text : '岗位权限分配',
				value : '',
				handler : function() {
					if(this.value!=''){
						var url = 'jsps/ma/power.jsp?jo_name=' + this.value;
					}else{
						var url = 'jsps/ma/power.jsp'
					}
					window.open(basePath + url, '_blank');
					}
				},
			{
			    xtype: 'label',
				/* forId: 'myFieldId',*/
				id:'myFieldIdssss',
				text: '',
				margin: '0 0 0 10'
			},
			 {
				xtype : 'textareafield',
				grow : true,
				id : 'titleRemort',
				name : 'message',
				margin:'8 0 0 0',
				/*fieldLabel : '<br>&nbsp&nbsp&nbsp岗位描述',*/
				/*readOnly : true,*/
				anchor : '100% 80%'
			}]
		}, {
			xtype : 'tabpanel',
			anchor : '100% 70%',
			height : 320,
			minHeight : 300,
			items : [{
				title : '岗位人员',
				xtype : 'jobpersongrid',
				height : 250,
				minheight : 100
			}, {
				title : '岗位权限',
				xtype : 'form',
				height : 200,
				id : 'saasjobpowerform',
				layout : 'fit',
				items : {
					xtype : 'htmleditor',
					id:'saasjobpower',
					enableColors : false,
					enableAlignments : false,
					/*readOnly : true*/
				}
			}]
		} ]
	},{
		region:'west',
		title:'岗位列表',
		xtype:'jobsetgrid',
		plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToMoveEditor: 1,        
			autoCancel: false
		})],
		animCollapse: true,
		collapsible: true,
		width: 303,
		fieldWidth:150,
		margins: '0 5 0 0',
		caller:'Job',
		field:'jo_name',
		
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});