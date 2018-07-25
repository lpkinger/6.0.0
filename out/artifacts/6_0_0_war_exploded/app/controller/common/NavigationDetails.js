Ext.QuickTips.init();
Ext.define('erp.controller.common.NavigationDetails', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','common.NavigationDetails','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber'
      	],
    init:function(){
    	var me = this;
    	me.caller="";
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'tabpanel':{
    			afterrender: function(tb) {	
    				this.getNavigationDetails(tb, id);
    			}
    		}
    	});
    },
    getNavigationDetails:function(tb, id) {
    	var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/getNavigationDetails.action',
			params: {
				id: id
			},
			callback: function(opt, s, r) {
				var res = Ext.decode(r.responseText);
				var url,pagedesc,servicedesc,pageFlag=true,html_page='',html_servive='';
				if(res.data){
					pagedesc=res.data.pagedesc;
					servicedesc=res.data.servicedesc;
					me.caller=res.data.caller;
					leaf=res.data.leaf;
					if(pagedesc!=''){
						 var suffix=pagedesc.substring(pagedesc.lastIndexOf(".")+1);
						 if(suffix=='pdf'){
						 	 html_page='<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/oa/doc/read.jsp?path='+ pagedesc + '&folderId=-1 " height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
						 }else if(suffix == 'doc'|| suffix =='docx'){
							Ext.Ajax.request({
								url : basePath + 'oa/doc/getHtml.action',
								params: {
									folderId:0,
									path:pagedesc,
									type:suffix
								},
								method : 'post',
								async:false,
								callback : function(opt, s, res){
								var r = new Ext.decode(res.responseText);
								if(r.exceptionInfo){
									showError(r.exceptionInfo);
								} else if(r.success){
									path=r.newPath;
									html_page='<iframe id="iframe_maindetail_pageSet" src="' +
									basePath + 'jsps/oa/doc/readWordOrExcel.jsp?path='+
									basePath+ path + ' " height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
								} 
							}
							});	
						
						 }
					}else{
						html_page='<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/ma/helpdocremind.html" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
					}
					if(servicedesc!=''){
						 var suffix=servicedesc.substring(servicedesc.lastIndexOf(".")+1);
						 if(suffix=='pdf'){
						 	 html_servive='<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/oa/doc/read.jsp?path='+ servicedesc +'&folderId=-1 " height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
						 }else if(suffix == 'doc'|| suffix =='docx'){
							Ext.Ajax.request({
								url : basePath + 'oa/doc/getHtml.action',
								params: {
									folderId:0,
									path:servicedesc,
									type:suffix
								},
								method : 'post',
								async:false,
								callback : function(opt, s, res){
								var r = new Ext.decode(res.responseText);
								if(r.exceptionInfo){
									showError(r.exceptionInfo);
								} else if(r.success){
									path=r.newPath;
									html_servive='<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/oa/doc/readWordOrExcel.jsp?path='+basePath+ servicedesc + ' " height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
								} 
							}
							});	
						 }
					}else{
						html_servive='<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/ma/helpdocremind.html" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
					}
					if(res.data.url) {
						if(me.FormUtil.contains(res.data.url,'jsps/common/print.jsp')||//打印界面
						me.FormUtil.contains(res.data.url,'jsps/common/search.jsp')){//search查询界面
							pageFlag=false;//不添加单据展示
						}
						if(me.FormUtil.contains(res.data.url, 'jsps/common/datalist.jsp', true)
						&&me.FormUtil.contains(res.data.url, '&urlcondition', true)){//列表
							url=res.data.url.split('&urlcondition')[0]+'&urlcondition'+res.data.url.split('&urlcondition')[1].split('&')[0]+' and 1=2';//&_noc=1
						}else if(me.FormUtil.contains(res.data.url, 'jsps/common/datalist.jsp', true)){
							url=res.data.url+'&urlcondition=1=2';//&_noc=1
						}else if(me.FormUtil.contains(res.data.url, '?', true)){
							url=res.data.url+'&source=allnavigation';
						}else{
							url=res.data.url+'?source=allnavigation';
						}
						url=url+'&_noc=1';
						url=me.parseUrl(url);
						if(url&&pageFlag){
							tb.add({
								title:'界面展示',
								tag : 'iframe',
								frame : true,
				    			border : false,
				    			layout : 'fit',
				    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				    			
							});
						}
					}
					tb.add({
						title:leaf=='T'?'单据说明':'模块说明',
			    		layout : 'anchor',
			    		tbar:[{xtype : "tbfill"},{xtype:'form',
					    		bodyStyle: 'background:#f1f1f1;',
					    		name:'pagedesc',
					    		anchor: '100% 6%',
					    		layout:'column',
					    		border:false,
					    		items:[{ xtype: 'filefield', 
							    		name: 'file',width:120,
							    		buttonText: '<img align="center" src="../../resource/images/icon/upload.png"/>上传说明文档',buttonOnly: true,hideLabel: true,
									  	listeners: {
											change: function(field){
												if(field.value != null){
													me.upload(field.ownerCt, field,'pagedesc');
												}
											},
											render: function (field, p) {
												Ext.QuickTips.register({
								                    target: field.el,
								                    text: '只能上传doc或pdf类型文件！'
								              	});
											}
									  	}
								  },{xtype: 'button',text:'下载',width:70,height:26,icon:'../../resource/images/icon/download.png',
								  	href:basePath + 'common/downloadPageinstruction.action?path=' + pagedesc+'&id='+id+'&field=pagedesc',
									   listeners: {
											afterrender: function(f){
												if(!pagedesc) f.hide();
											 }
										  }
								  }],
								  listeners: {
									  afterrender: function(f){
										if(em_type!='admin') f.hide();
									  }
								  }
						  }],
			    		items:[{
						  	tag : 'iframe',
							frame : true,
				    		border : false,
				    		layout : 'fit',
				    		id:'pagedesciframe',
				    		anchor: em_type=='admin'?'100% 94%':'100% 100%',
				    		html :html_page				    	
						  }]
					},{
						title:'业务说明',
				    	layout : 'anchor',
				    	tbar:[{xtype : "tbfill"},{xtype:'form',border:false,
			    				layout:'column',
					    		bodyStyle: 'background:#f1f1f1;',
					    		name:'servicedesc',
					    		anchor: '100% 6%',
					    		items:[{ xtype: 'filefield', 
							    		name: 'file',
							    		buttonText: '<img align="center" src="../../resource/images/icon/upload.png"/>上传说明文档',buttonOnly: true,hideLabel: true,width:120,
									  	listeners: {
											change: function(field){
												if(field.value != null){
													me.upload(field.ownerCt, field,'servicedesc');
												}
											},
											render: function (field, p) {
												Ext.QuickTips.register({
								                    target: field.el,
								                    text: '只能上传doc或pdf类型文件！'
								              	});
											}
									  	}
								  },{xtype: 'button',text:'下载',width:70,height:26,icon:'../../resource/images/icon/download.png',
								  	href:basePath + 'common/downloadPageinstruction.action?path=' + servicedesc+'&id='+id+'&field=servicedesc',
									  listeners: {
										  afterrender: function(f){
											if(!servicedesc) f.hide();
										  }
									  }
								  }],
								  listeners: {
									  afterrender: function(f){
										if(em_type!='admin') f.hide();
									  }
								  }
						  }],
			    		items:[{
						  	tag : 'iframe',
							frame : true,
							id:'servicedesciframe',
				    		border : false,
				    		layout : 'fit',
				    		anchor: em_type=='admin'?'100% 94%':'100% 100%',
				    		html : html_servive
						  }]
						});
						tb.add({
								title:'权限申请',
								tag : 'iframe',
								frame : true,
								id:'powerApplyTab',
				    			border : false,
				    			layout : 'fit',
				    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + 'jsps/oa/powerApply/powerApply.jsp?_noc=1' + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				    			
							});
				}				
			}
		});
    },
    upload:function(form, field,fieldname){
		var me = this;
		var filename = '';
		if(contains(field.value, "\\", true)){
			filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
		} else {
			filename = field.value.substring(field.value.lastIndexOf('/') + 1);
		}
		if(!me.checkFile(filename)){
			showError('当前类型文件不允许上传,只能上传doc或pdf类型文件!');
			return false;
		}
		form.getForm().submit({
			url: basePath + 'common/uploadPageinstruction.action',
			params:{
				caller:me.caller,
				field:fieldname,
				id:id
			},
			waitMsg: "正在上传:" + filename,
			success: function(fp, o){
				if(o.result.error){
					showError(o.result.error);
				} else {
					Ext.Msg.alert("恭喜", filename + " 上传成功!",function(){window.location.reload();});	
				}
			}
		});
	},
	checkFile:function(fileName){
		var arr=['pdf','doc'];
	    var suffix=fileName.substring(fileName.lastIndexOf(".")+1);
	    return Ext.Array.contains(arr,suffix);
	},
	parseUrl: function(url) {
        var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
        if (id == null) {
            id = url.substring(0, url.lastIndexOf('.'));
        }
        if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
            url = url.replace(/session:em_uu/g, em_uu);
        }
        if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
            url = url.replace(/session:em_code/g, "'" + em_code + "'");
        }
        if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
            url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
        }
        if (contains(url, 'session:em_name', true)) {
            url = url.replace(/session:em_name/g, "'" + em_name + "'");
        }
        if (contains(url, 'session:em_type', true)) {
            url = url.replace(/session:em_type/g, "'" + em_type + "'");
        }
        if (contains(url, 'session:em_id', true)) {
            url = url.replace(/session:em_id/g,em_id);
        }
        if (contains(url, 'session:em_depart', true)) {
            url = url.replace(/session:em_depart/g,em_id);
        }
        return url;
    }
});