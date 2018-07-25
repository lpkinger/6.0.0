Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.HrOrgStr', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.HrOrgStr','hr.employee.HrOrgStrTree',
    		'core.form.YnField',
    		'core.form.Panel',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.grid.Panel2',
    		'hr.emplmana.EducationGrid','hr.emplmana.PositionGrid','hr.emplmana.WorkGrid',
    		'hr.emplmana.ReandpunishGrid','core.grid.YnColumn','core.toolbar.Toolbar'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({ 
    		'hrOrgStrTree': {
    			itemmousedown:function(selModel,record){
    				var emid,emcode='';
    				if(record.data.leaf){
    					emid = record.data.id;
    					if(record.data.id.indexOf('-')>0)emid=emid.split('-')[0]; 
						me.getFormData(emid, emcode);  			
    				} else {
	    				var parentId=record.data.id;
			            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
            			    record.collapse(true, true); //收拢
			            } else { //未展开
            		    //看是否加载了其children
	                		if (record.childNodes.length == 0) {
	                    		//从后台加载
	                    		var tree = Ext.getCmp('tree-panel');
	                   			tree.setLoading(true, tree.body);
	                   			Ext.Ajax.request({ //拿到tree数据
	                    		    url: basePath + 'hr/employee/getAllHrOrgsTree.action',
			                        params: {
			                            	parentId: parentId
			                        },
	                       		 callback: function(options, success, response) {
	                          		  tree.setLoading(false);
	                          		  var res = new Ext.decode(response.responseText);
	                         		  if (res.tree) {
		                                record.appendChild(me.setTreeFirstLeafCls(res.tree));
		                                record.expand(false, true); //展开
	                          		  } else if (res.exceptionInfo) {
	                               		 showError(res.exceptionInfo);
	                            	}
	                        	}
	                    	});
               		 } else {
		                    record.expand(false, true); //展开
		                }
		            }
		        }
    			}
    		},
    		'erpFormPanel': {
    			afterrender: function(f){
    				f.readOnly = true;
    				var items = f.items.items;
    				Ext.each(items, function(item){
    					item.setFieldStyle('background:#f1f1f1;');
    					item.setReadOnly(true);
    				});
    			}
    		}
    	});
    },
    setTreeFirstLeafCls:function(tree){
    	var arry = tree;
		if (arry[0].leaf==true) {
			arry[0].cls = "x-hrorgTreeFirstLeaf";	
		}
		return arry;
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    getFormData: function(id, code){
    	var condition = '';
    	if(id==null){
    		condition = "em_code='" + code + "'";
    	} else {
    		condition = 'em_id=' + id;
    	}
    	Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'hr/employee/getEmployee.action',
        	params: {
        		condition: condition
        	},
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
        			var employee = res.employee;
        			Ext.getCmp('em_name').setValue(employee.em_name);
        			Ext.getCmp('em_sex').setValue(employee.em_sex == '男' || employee.em_sex == 'M' ? '男':'女');
        			Ext.getCmp('em_birthday').setValue(Ext.util.Format.date(new Date(employee.em_birthday),'Y-m-d H:i:s'));
        			Ext.getCmp('em_indate').setValue(Ext.util.Format.date(new Date(employee.em_indate),'Y-m-d H:i:s'));
        			Ext.getCmp('em_nation').setValue(employee.em_nation);
        			Ext.getCmp('em_polity').setValue(employee.em_polity);
        			Ext.getCmp('em_native').setValue(employee.em_native);
        			Ext.getCmp('em_blood').setValue(employee.em_blood);
        			Ext.getCmp('em_weight').setValue(employee.em_weight);
        			Ext.getCmp('em_height').setValue(employee.em_height);
        			Ext.getCmp('em_marry').setValue(employee.em_marry == -1 ? '未婚':'已婚');
        			Ext.getCmp('em_iccode').setValue(employee.em_iccode);
        			Ext.getCmp('em_worktime').setValue(employee.em_worktime == 0 ? '没有工作经验':(employee.em_worktime + ' 年'));
        			Ext.getCmp('em_email').setValue(employee.em_email);
        			Ext.getCmp('em_address').setValue(employee.em_address);
        			Ext.getCmp('em_heathlevel').setValue(employee.em_heathlevel);
        			Ext.getCmp('em_speciality').setValue(employee.em_speciality);
        			Ext.getCmp('em_finishschool').setValue(employee.em_finishschool);
        			Ext.getCmp('em_culture').setValue(employee.em_culture);
        			Ext.getCmp('em_mobile').setValue(employee.em_mobile);
        			Ext.getCmp('em_position').setValue(employee.em_position);
        			Ext.getCmp('em_defaultorname').setValue(employee.em_defaultorname);
        			Ext.getCmp('em_depart').setValue(employee.em_depart);
        			Ext.getCmp('em_code').setValue(employee.em_code);
        			Ext.getCmp('em_professname').setValue(employee.em_professname);
        			Ext.getCmp('em_class').setValue(employee.em_class);
        			Ext.getCmp('em_ctel').setValue(employee.em_ctel);
        			Ext.getCmp('em_contact').setValue(employee.em_contact); 
        			Ext.getCmp('em_tel').setValue(employee.em_tel);
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
    }
});