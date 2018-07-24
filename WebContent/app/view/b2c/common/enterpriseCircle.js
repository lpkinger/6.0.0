Ext.QuickTips.init();
/**
 * 添加合作伙伴
 */
var enterpriseStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'businessCode',
		type : 'string'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'shortName',
		type : 'string'
	}, {
		name : 'address',
		type : 'string'
	},{
		name : 'requestStatus',
		type : 'string'
	},{
		name : 'method',
		type : 'string'
	},{
		name : 'adminName',
		type : 'string'
	},{
		name : 'adminTel',
		type : 'string'
	},{
		name : 'industry',
		type : 'string'
	},{
		name : 'type',
		type : 'string'
	},{
		name : 'adminEmail',
		type : 'string'
	},{
		name : 'requestStatus',
		type : 'string'
	},{
		name : 'method',
		type : 'number'
	},{
		name : 'profession',
		type : 'string'
	},{
		name : 'tags',
		type : 'string'
	},{
		name : 'corporation',
		type : 'string'
	},{
		name : 'uu',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/enterpriseList.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("enterpriseListGrid");
			var keyword = Ext.getCmp('enterpriseSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});
/**
 * 全部合作伙伴
 */
var partnerStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'vendName',
		type : 'string'
	}, {
		name : 'vendUID',
		type : 'string'
	}, {
		name : 'custUserName',
		type : 'string'
	}, {
		name : 'vendUserName',
		type : 'string'
	}, {
		name : 'reason',
		type : 'string'
	},{
		name : 'statusCode',
		type : 'number'
	},{
		name : 'id',
		type : 'number'
	},{
		name : 'method',
		type : 'string'
	},{
		name : 'appId',
		type:'string'
	},{
		name :　'uu',
		type : 'string'
	},{
		name : 'enBussinessCode',
		type : 'string'
	},{
		name : 'enCorporation',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/myPartners.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("myPartnerGrid");
			var keyword = Ext.getCmp('partnerSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword,
				statusCode : 313
			});
		}
	}
});
/**
 * 新的合作伙伴
 */
var partnerStore1 = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'vendName',
		type : 'string'
	}, {
		name : 'vendUID',
		type : 'string'
	}, {
		name : 'custUserName',
		type : 'string'
	}, {
		name : 'vendUserName',
		type : 'string'
	}, {
		name : 'reason',
		type : 'string'
	},{
		name : 'statusCode',
		type : 'number'
	},{
		name : 'id',
		type : 'number'
	},{
		name : 'method',
		type : 'string'
	},{
		name : 'appId',
		type:'string'
	},{
		name :　'uu',
		type : 'string'
	},{
		name : 'enBussinessCode',
		type : 'string'
	},{
		name : 'enCorporation',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/getNewPartners.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("myPartnerGrid1");
			var keyword = Ext.getCmp('partnerSearch1').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword,
				statusCode : null
			});
		}
	}
});
/**
 * 平台供应商
 */
var vendorStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 've_id',
		type : 'string'
	},{
		name : 've_name',
		type : 'string'
	},{
		name : 've_add1',
		type : 'string'
	},{
		name : 've_contact',
		type : 'string'
	},{
		name : 've_tel',
		type : 'string'
	},{
		name : 'appId',
		type : 'string'
	},{
		name : 've_uu',
		type : 'string'
	},{
		name : 've_legalman',
		type : 'string'
	},{
		name : 've_webserver',
		type : 'string'
	},{
		name : 've_email',
		type : 'string'
	},{
		name : 've_businessrange',
		type : 'string'
	},{
		name : 've_industry',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/vendors.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("vendorGrid");
			var keyword = Ext.getCmp('vendorSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});
/**
 * 客户
 */
var customerStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'cu_id',
		type : 'string'
	},{
		name : 'cu_name',
		type : 'string'
	},{
		name : 'cu_add1',
		type : 'string'
	},{
		name : 'cu_contact',
		type : 'string'
	},{
		name : 'cu_tel',
		type : 'string'
	},{
		name : 'appId',
		type : 'string'
	},{
		name : 'cu_uu',
		type : 'string'
	},{
		name : 'cu_lawman',
		type : 'string'
	},{
		name : 'cu_email',
		type : 'string'
	},{
		name : 'cu_businesscode',
		type : 'string'
	},{
		name : 'cu_industry',
		type : 'string'
	},{
		name : 'cu_mainbusiness',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/customers.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("customerGrid");
			var keyword = Ext.getCmp('customerSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});
/**
 * 邀请注册记录
 */
var invitationStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'vendname',
		type : 'string'
	},{
		name : 'vendusername',
		type : 'string'
	},{
		name : 'vendusertel',
		type : 'string'
	},{
		name : 'venduseremail',
		type : 'string'
	},{
		name : 'date',
		type : 'string'
	},{
		name : 'vendusername',
		type : 'string'
	},{
		name : 'active',
		type : 'number'
	}/*,{
		name : 'count',
		type : 'string'
	}*/],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/invitations.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("invitationGrid");
			var keyword = Ext.getCmp('invitationSearch').value;
			var rg1 = Ext.getCmp('rg1');
			var rg2 = Ext.getCmp('rg2');
			var value = 0;
			if(rg2.value==true){
				value = 1;
			}
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword,
				value : value
			});
		}
	}
});
/**
 * ERP供应商
 */
var erpVendorStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 've_id',
		type : 'string'
	},{
		name : 've_name',
		type : 'string'
	},{
		name : 've_shortname',
		type : 'string'
	},{
		name : 've_add1',
		type : 'string'
	},{
		name : 've_contact',
		type : 'string'
	},{
		name : 've_tel',
		type : 'string'
	},{
		name : 'appId',
		type : 'string'
	},{
		name : 've_uu',
		type : 'string'
	},{
		name : 've_webserver',
		type : 'string'
	},{
		name : 've_legalman',
		type : 'string'
	},{
		name : 've_email',
		type : 'string'
	},{
		name : 've_mobile',
		type : 'string'
	},{
		name : 've_businessrange',
		type : 'string'
	},{
		name : 've_industry',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/erpVendors.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("noPlatformGrid");
			var keyword = Ext.getCmp('noPlatformSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});
/**
 * ERP客户
 */
var eroCustomerStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 'cu_id',
		type : 'string'
	},{
		name : 'cu_name',
		type : 'string'
	},{
		name : 'cu_shortname',
		type : 'string'
	},{
		name : 'cu_add1',
		type : 'string'
	},{
		name : 'cu_contact',
		type : 'string'
	},{
		name : 'cu_tel',
		type : 'string'
	},{
		name : 'appId',
		type : 'string'
	},{
		name : 'cu_uu',
		type : 'string'
	},{
		name : 'cu_email',
		type : 'string'
	},{
		name : 'cu_businesscode',
		type : 'string'
	},{
		name : 'cu_lawman',
		type : 'string'
	},{
		name : 'cu_industry',
		type : 'string'
	},{
		name : 'cu_mainbusiness',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/erpCustomers.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("nocustomerGrid");
			var keyword = Ext.getCmp('nocustomerSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});
/**
 * 服务商
 */
var serviceStore = Ext.create('Ext.data.Store', {
	fields : [{
		name : 've_id',
		type : 'string'
	},{
		name : 've_name',
		type : 'string'
	},{
		name : 've_add1',
		type : 'string'
	},{
		name : 've_contact',
		type : 'string'
	},{
		name : 've_tel',
		type : 'string'
	},{
		name : 'appId',
		type : 'string'
	},{
		name : 've_uu',
		type : 'string'
	},{
		name : 've_legalman',
		type : 'string'
	},{
		name : 've_webserver',
		type : 'string'
	},{
		name : 've_email',
		type : 'string'
	},{
		name : 've_businessrange',
		type : 'string'
	},{
		name : 've_industry',
		type : 'string'
	}],
	autoLoad : false,
	pageSize : 20,
	proxy : {
		type : 'ajax',
		url : basePath + 'ac/services.action',
		reader : {
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var grid = Ext.getCmp("serviceGrid");
			var keyword = Ext.getCmp('serviceSearch').value;
			Ext.apply(grid.getStore().proxy.extraParams, {
				keyword : keyword
			});
		}
	}
});

Ext.define('erp.view.b2c.common.enterpriseCircle', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	cls : 'x-mall',
	initComponent : function() {		
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype:'tabpanel',
				//region:'center',
				id:'tabpanelOne',
				layout:'fit',
				cls:'top_tabbar',
				items:[{
					title:'我的平台伙伴',
					xtype : 'tabpanel',
					layout:'fit',
					cls:'top_tabbar_items',
					items:[{
						title:'全部合作伙伴',
						xtype : 'form',
						layout : 'border',
						items : [{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'partnerSearch',
								name : 'partnerSearch',
								emptyText : '输入企业名称等关键词进行搜索',
								region : 'north',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('myPartnerGrid');
								    	var keyword = Ext.getCmp('partnerSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							},{
								xtype : 'label',
								html : "<a class='btn-sync' style='text-decoration:none' href='#' onclick=sync() type='button'>一键同步</a>"
							}]
						},{
							xtype:'grid',
							id : 'myPartnerGrid',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: partnerStore,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex:'vendName',
							    align : 'left',
							    width : 240
							},{
							    text : '营业执照',
							    dataIndex:'vendUID',
							    align : 'left',
							    width : 200
							},{
								text : '法定代表人',
							    dataIndex:'enCorporation',
							    align : 'left',
							    width : 80,
							    renderer : function(m,v,r){
							    	if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.enCorporation;
							    	}
							    }
							},{
								text : '企业UU',
								align : 'left',
								width : 120,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
										return r.raw.enterprise.uu;
									}
								}
							},{
								text : '联系人',
								align : 'center',
								width : 80,
								renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userName;
							    	}
								}
							},{
								text : '电话',
								align : 'left',
								width : 120,
								renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userTel;
							    	}
								}
							},{
								text : '邮箱',
								align : 'left',
								width : 160,
								renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userEmail;
							    	}
								}
							},{
								text : '行业',
								align : 'left',
								width : 120,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
										return r.raw.enterprise.profession;
									}
								}
							},{
								text : '经营范围',
								align : 'left',
								width : 120,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
										return r.raw.enterprise.tags;
									}
								}
							},{
								text : '企业地址',
								align : 'left',
								width : 200,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.enAddress;
							    	}
								}
							},{
								text : '供应商',
								width:80,
								align : 'center',
								renderer : function(meta,value,record){
									var appid = record.data.appId,uu = record.data.uu;
									var id = record.data.id;
									var vendor = record.raw.vendor,vendswitch = record.raw.vendswitch,vendorId = record.raw.vendorId,vendUID=record.raw.vendUID;
									//判断是否建立关系。1：是    0：否
									var hasRelative = 1;
									if(vendor==1&&vendswitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='vendUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}else if(vendor==1&&vendswitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='vendUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' checked>";
									}else{
										hasRelative = 0;
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='vendUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}
								}
								
							},{
								text : '客户',
								width : 80,
								align : 'center',
								renderer : function(meta,value,record){
									var appid = record.data.appId,uu = record.data.uu,id = record.id;
									var customer = record.raw.customer,custswitch = record.raw.custswitch;
									var custId = record.raw.custId,vendUID=record.raw.vendUID,hasRelative = 1;
									//判断是否建立关系。1：是    0：否
									if(customer==1&&custswitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=customerUse('"+id+"','"+custId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='customerUse(\""+id+"\",\""+custId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' checked>";
									}else if(customer==1&&custswitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=customerUse('"+id+"','"+custId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='customerUse(\""+id+"\",\""+custId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}else{
										hasRelative = 0;
										//return "<input id='"+id+"' type='button' value='启用' onclick=customerUse('"+id+"','"+custId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='customerUse(\""+id+"\",\""+custId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}
								}
							},{
								text : '服务商',
								width : 80,
								align : 'center',
								renderer : function(meta,value,record){
									var appid = record.data.appId,uu = record.data.uu;
									var id = record.data.id+1;
									var vendor = record.raw.vendor,servicerswitch = record.raw.servicerswitch,vendorId = record.raw.vendorId,vendUID=record.raw.vendUID;
									//判断是否建立关系。1：是    0：否
									var hasRelative = 1;
									if(servicerswitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='serviceUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}else if(servicerswitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='serviceUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' checked>";
									}else{
										hasRelative = 0;
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='serviceUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : partnerStore,
								emptyMsg : "暂无数据"
							})
						}]
					},{
						title:'供应商',
						xtype : 'form',
						layout : 'border',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'vendorSearch',
								name : 'vendorSearch',
								emptyText : '输入企业名称等关键词进行搜索',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('vendorGrid');
								    	var keyword = Ext.getCmp('vendorSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							}]
						},{
							xtype:'grid',
							id : 'vendorGrid',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: vendorStore,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1'
							}),{
							    text : '企业名称',
							    dataIndex:'ve_name',
							    align : 'left',
							    width : 260
							},{
								text : '营业执照',
								dataIndex : 've_webserver',
								align : 'left',
								width : 180
							},{
								text : '法定代表人',
								dataIndex : 've_legalman',
								align : 'center'
							},{
								text : '企业UU',
								dataIndex : 've_uu',
								align : 'left',
								width : 100
							},{
							    text : '联系人',
							    dataIndex:'ve_contact',
							    align : 'center'
							},{
							    text : '电话',
							    dataIndex:'ve_tel',
							    align : 'left',
							    width : 120
							},{
								text : '邮箱',
								dataIndex : 've_email',
								align : 'left',
								width : 180
							},{
								text : '行业',
								dataIndex : 've_industry',
								align : 'left',
								width : 120
							},{
								text : '经营范围',
								dataIndex : 've_businessrange',
								align : 'left',
								width : 120
							},{
							    text : '企业地址',
							    dataIndex:'ve_add1',
							    align : 'left',
							    width : 200
							},{
							    text : '操作',
							    width: 120,
							    align: 'center',
								renderer: function (meta, val,record) {
									var uu = record.data.ve_uu;
									var id = record.id;
									var vendswitch = record.raw.vendorSwitch,vendorId = record.raw.b2b_vendor_id,vendUID=null,grid = "vendorGrid";
									//判断是否建立关系。1：是    0：否
									var hasRelative = 1;
									if(vendswitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='vendUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}else if(vendswitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='vendUse(\""+id+"\",\""+vendorId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' checked>";
									}
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : vendorStore,
								emptyMsg : "暂无数据"
							})
						}]
					},{
						title:'客户',
						xtype : 'form',
						layout : 'border',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'customerSearch',
								name : 'customerSearch',
								emptyText : '输入企业名称等关键词进行搜索',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('customerGrid');
								    	var keyword = Ext.getCmp('customerSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							}]
						},{
							xtype:'grid',
							id : 'customerGrid',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: customerStore,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex:'cu_name',
							    align : 'left',
							    width : 220
							},{
								text : '营业执照',
							    dataIndex:'cu_businesscode',
							    align : 'left',
							    width : 180
							},{
								text : '法定代表人',
							    dataIndex:'cu_lawman',
							    align : 'center',
							    width : 80
							},{
								text : '企业UU',
							    dataIndex:'cu_uu',
							    align : 'left',
							    width : 100
							},{
							    text : '联系人',
							    dataIndex:'cu_contact',
							    align : 'center'
							},{
							    text : '电话',
							    dataIndex:'cu_tel',
							    align : 'left',
							    width : 120
							},{
								text : '邮箱',
							    dataIndex:'cu_email',
							    align : 'left',
							    width : 220
							},{
								text : '行业',
							    dataIndex:'cu_industry',
							    align : 'left',
							    width : 180
							},{
								text : '经营范围',
							    dataIndex:'cu_mainbusiness',
							    align : 'left',
							    width : 180
							},{
							    text : '企业地址',
							    dataIndex:'cu_add1',
							    align : 'left',
							    width : 180
							},{
							    text : '操作',
							    width: 120,
							    align: 'center',
								renderer: function (meta, val,record) {
									var uu = record.data.cu_uu,id = record.id;
									var custswitch = record.raw.customerSwitch;
									var custId = record.raw.b2b_vendor_id,vendUID=record.raw.vendUID,hasRelative = 1,grid = "customerGrid";
									//判断是否建立关系。1：是    0：否
									if(custswitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=customerUse('"+id+"','"+custId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='customerUse(\""+id+"\",\""+custId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' checked>";
									}else if(custswitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=customerUse('"+id+"','"+custId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='customerUse(\""+id+"\",\""+custId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : customerStore,
								emptyMsg : "暂无数据"
							})
						}]					
					},{
						title:'服务商',
						xtype : 'form',
						layout : 'border',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'serviceSearch',
								name : 'serviceSearch',
								emptyText : '输入企业名称等关键词进行搜索',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('serviceGrid');
								    	var keyword = Ext.getCmp('serviceSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							}]
						},{
							xtype:'grid',
							id : 'serviceGrid',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: serviceStore,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1'
							}),{
							    text : '企业名称',
							    dataIndex:'ve_name',
							    align : 'left',
							    width : 240
							},{
								text : '营业执照',
								dataIndex : 've_webserver',
								align : 'left',
								width : 180
							},{
								text : '法定代表人',
								dataIndex : 've_legalman',
								align : 'center'
							},{
								text : '企业UU',
								dataIndex : 've_uu',
								align : 'left',
								width : 120
							},{
							    text : '联系人',
							    dataIndex:'ve_contact',
							    align : 'center'
							},{
							    text : '电话',
							    dataIndex:'ve_tel',
							    align : 'left'
							},{
								text : '邮箱',
								dataIndex : 've_email',
								align : 'left',
								width : 200
							},{
								text : '行业',
								dataIndex : 've_industry',
								align : 'left',
								width : 120
							},{
								text : '经营范围',
								dataIndex : 've_businessrange',
								align : 'left',
								width : 120
							},{
							    text : '企业地址',
							    dataIndex:'ve_add1',
							    align : 'left',
							    width : 200
							},{
							    text : '操作',
							    width: 120,
							    align: 'center',
								renderer: function (meta, val,record) {
									var id = record.id;
									var servicerSwitch = record.raw.servicerSwitch,serviceId = record.raw.b2b_vendor_id,vendUID=null,grid = "serviceGrid";
									//判断是否建立关系。1：是    0：否
									var hasRelative = 1;
									if(servicerSwitch==0){
										//return "<input id='"+id+"' type='button' value='启用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='serviceUse(\""+id+"\",\""+serviceId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' unchecked>";
									}else if(servicerSwitch==1){
										//return "<input id='"+id+"' type='button' value='禁用' onclick=vendUse('"+id+"','"+vendorId+"','"+hasRelative+"','"+vendUID+"'); >";
										return "<input class='mui-switch' onchange='serviceUse(\""+id+"\",\""+serviceId+"\",\""+hasRelative+"\",\""+vendUID+"\",\""+grid+"\")'  type='checkbox'  id='"+id+"' checked>";
									}
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : serviceStore,
								emptyMsg : "暂无数据"
							})
						}]
					}]
				},{
					title : 'UAS合作伙伴',
					xtype : 'tabpanel',
					layout : 'fit',
					cls:'top_tabbar_items',
					items : [{
						title : '供应商',
						xtype : 'form',
						layout : 'border',
							items : [{
								xtype : 'form',
								layout : 'column',
								region: 'north',
								height: 35,
								items:[{
									xtype : 'textfield',
									cls : 'test',
									id : 'noPlatformSearch',
									name : 'noPlatformSearch',
									emptyText : '输入企业名称、联系人搜索',
									height : 30,
									width : 500
								},{
									xtype : 'button',
									text : "<p style='color:white'>搜索</p>",
									cls : 'btn-search',
									scale : 'medium',
									listeners:{
										click : function(){
											var grid = Ext.getCmp('noPlatformGrid');
									    	var keyword = Ext.getCmp('noPlatformSearch').value;
									    	Ext.apply(grid.getStore().proxy.extraParams, {
									    		keyword: keyword
											});
									    	var toolbar = grid.dockedItems.items[1];
									    	toolbar.moveFirst();
										}
									}
								}]
							},{
								xtype:'grid',
								id : 'noPlatformGrid',
								autoScroll:true,
								region: 'center',
								columnLines : true,
								store: erpVendorStore,
								columns : [Ext.create('Ext.grid.RowNumberer' , {
									text : '序号',
									width : 50 ,
									height :25 ,
									align : 'center',
									cls : 'x-grid-header-1'
								}),{
								    text : '企业名称',
								    dataIndex:'ve_name',
								    align : 'left',
								    width : 300,
								    renderer : function(meta,value,record){
								    	var b2b = record.raw.b2b;
								    	var ve_name = record.raw.ve_name,ve_id = record.raw.ve_id;
								    	if(b2b==1){
								    		return ve_name;
								    	}else{
								    		var url_ = "jsps/scm/purchase/vendor.jsp?formCondition=ve_id="+ve_id+"";
											return '<a href="javascript:openUrl(\'' + url_ + '\');">' + ve_name + '</a>';
								    	}
								    }
								},{
									text : '简称',
									dataIndex : 've_shortname',
									aligh : 'left'
								},{
									text : '营业执照',
									dataIndex : 've_webserver',
									align : 'left',
									width : 200
								},{
									text : '法定代表人',
									dataIndex : 've_legalman',
									align : 'center'
								},{
								    text : '联系人',
								    dataIndex:'ve_contact',
								    align : 'center'
								},{
								    text : '电话',
								    dataIndex:'ve_tel',
								    align : 'left'
								},{
									text : '邮箱',
									dataIndex : 've_email',
									align : 'left',
									width : 200
								},{
								    text : '企业地址',
								    dataIndex:'ve_add1',
								    align : 'left',
								    width : 300
								},{
								    text : '操作',
								    width: 120,
								    align: 'center',
									renderer: function (meta, val,record) {
										var name = record.get('ve_name');
								    	var contact = record.get('ve_contact');
								    	var tel = record.get('ve_tel');
								    	var mail = record.get('ve_email');
								    	var partnerInfo = {};
								    	partnerInfo.name = name ;
								    	partnerInfo.adminName = contact ;
								    	partnerInfo.adminTel = tel ;
								    	partnerInfo.adminEmail = mail ;
								    	var info = Ext.JSON.encode(partnerInfo);
										return "<a class='btn btn-primary' href='#' onclick=invite('"+info+"','noPlatformGrid') type='button'>立即邀请注册</a>";
									}
								}],
								bbar : Ext.create('Ext.PagingToolbar', {
									store : erpVendorStore,
									emptyMsg : "暂无数据"
								})
						}]
					},{
						title:'客户',
						xtype : 'form',
						layout : 'border',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'nocustomerSearch',
								name : 'nocustomerSearch',
								emptyText : '输入企业名称、联系人搜索',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('nocustomerGrid');
								    	var keyword = Ext.getCmp('nocustomerSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							}]
						},{
							xtype:'grid',
							id : 'nocustomerGrid',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: eroCustomerStore,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex:'cu_name',
							    align : 'left',
							    width : 300,
							    renderer : function(meta,value,record){
							    	var b2b = record.raw.b2b;
							    	var cu_name = record.raw.cu_name,cu_id = record.raw.cu_id;
							    	if(b2b==1){
							    		return cu_name;
							    	}else{
							    		var url_ = "jsps/scm/sale/customerBase.jsp?formCondition=cu_id="+cu_id+"";
										return '<a href="javascript:openUrl(\'' + url_ + '\');">' + cu_name + '</a>';
							    	}
							    }
							},{
								text : '简称',
								dataIndex : 'cu_shortname',
								aligh : 'left'
							},{
								text : '营业执照',
								dataIndex : 'cu_businesscode',
								align : 'left',
								width : 200
							},{
								text : '法定代表人',
								dataIndex : 'cu_lawman',
								align : 'center'
							},{
							    text : '联系人',
							    dataIndex:'cu_contact',
							    align : 'center'
							},{
							    text : '电话',
							    dataIndex:'cu_tel',
							    align : 'left'
							},{
								text : '邮箱',
								dataIndex : 'cu_email',
								align : 'left',
								width : 200
							},{
							    text : '企业地址',
							    dataIndex:'cu_add1',
							    align : 'left',
							    width : 300
							},{
							    text : '操作',
							    width: 120,
							    align: 'center',
								renderer: function (meta, val,record) {
									var name = record.get('cu_name');
							    	var contact = record.get('cu_contact');
							    	var tel = record.get('cu_tel');
							    	var mail = record.get('cu_email');
							    	var partnerInfo = {};
							    	partnerInfo.name = name ;
							    	partnerInfo.adminName = contact ;
							    	partnerInfo.adminTel = tel ;
							    	partnerInfo.adminEmail = mail ;
							    	var info = Ext.JSON.encode(partnerInfo);
									return "<a class='btn btn-primary' href='#' onclick=invite('"+info+"','nocustomerGrid') type='button'>立即邀请注册</a>";
								
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : eroCustomerStore,
								emptyMsg : "暂无数据"
							})
						}]					
					
					}]
				},{
					title:'新的合作伙伴',
					xtype : 'tabpanel',
					id : 'maintab',
					layout:'fit',
					cls:'top_tabbar_items',
					items:[{
						title:'新的合作伙伴',
						xtype : 'form',
						layout : 'border',
						items : [{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'partnerSearch1',
								name : 'partnerSearch1',
								emptyText : '输入企业名称等关键词进行搜索',
								region : 'north',
								height : 30,
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('myPartnerGrid1');
								    	var keyword = Ext.getCmp('partnerSearch1').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword,
								    		statusCode : null
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							}]
						},{
							xtype : 'grid',
							id : 'myPartnerGrid1',
							autoScroll:true,
							region: 'center',
							columnLines : true,
							store: partnerStore1,
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex:'vendName',
							    align : 'left',
							    width : 300,
							    renderer : function(val,meta,record){
							    	var statuscode = record.get('statusCode');
									var method = record.get('method');
									var name = record.get('vendName');
							    	if(statuscode==311&&method==0){
										return name+"<img style='margin-bottom:6px;margin-left:3px' src="+basePath+"/resource/images/111.png>";
									}else{
										return name;
									}
							    }
							},{
							    text : '营业执照',
							    dataIndex:'vendUID',
							    align : 'left',
							    width : 250
							},{
							    text : '法定代表人',
							    dataIndex:'enCorporation',
							    align : 'center'
							},{
								text : '企业UU',
								align : 'left',
								width : 150,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.uu;
							    	}
								}
							},{
							    text : '联系人',
							    align : 'center',
							    renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userName;
							    	}
								}
							},{
							    text : '电话',
							    align : 'center',
							    renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userTel;
							    	}
								}
							},{
							    text : '邮箱',
							    align : 'center',
							    renderer : function(m,v,r){
									if(r.raw.contact!=null){
							    		return r.raw.contact.userEmail;
							    	}
								}
							},{
								text : '行业',
								align : 'left',
								width : 200,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.profession;
							    	}
								}
							},{
								text : '经营范围',
								align : 'left',
								width : 200,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.tags;
							    	}
								}
							},{
								text : '企业地址',
								align : 'left',
								width : 200,
								renderer : function(m,v,r){
									if(r.raw.enterprise!=null){
							    		return r.raw.enterprise.enAddress;
							    	}
								}
							},{
								text : '操作/状态',
							    width: 200,
							    align: 'center',
								renderer:function(meta,value,record){
									var statuscode = record.get('statusCode');
									var method = record.get('method');
									var name = record.get('vendName');
									var id = record.get('id');
							    	var businessCode = record.get('vendUID');
							    	var partnerInfo = {};
							    	partnerInfo.name = name ;
							    	partnerInfo.businessCode= businessCode;
							    	partnerInfo.id = id ;
							    	var info = Ext.JSON.encode(partnerInfo);
									if(statuscode==311){
										if(method==0){
											return "<input type='button' value='同意' onclick=agreeRequest("+id+");> || " +
											"<input type='button' value='拒绝' onclick=refuseRequest("+id+");>";
										}else if(method==1){
											return "待回复";
										}
									}
									if(statuscode==313){
										if(method==0){
											return "已同意";
										}else if(method==1){
											return "已通过";
										}
									}
									if(statuscode==310){
										if(method==0){
											return "已拒绝";
										}else if(method==1){
											return "未通过/<input type='button' value='再次添加' onclick=newPartners('"+info+"','myPartnerGrid1');>";
										}
									}
								}
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								id:'pagingbar1',
								store : partnerStore1,
								emptyMsg : "暂无数据"
							}),
							listeners: {//滚动条有时候没反应，添加此监听器
								scrollershow: function(scroller) {
									if (scroller && scroller.scrollEl) {
										scroller.clearManagedListeners();  
										scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
									}
								}
							}
						}]
					},{
						title:'邀请注册记录',
						xtype : 'form',
						layout : 'border',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'invitationSearch',
								name : 'invitationSearch',
								emptyText : '输入企业名称、简称、联系人、邀请人搜索',
								height : 30,
								width : 500	
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
								listeners:{
									click : function(){
										var grid = Ext.getCmp('invitationGrid');
								    	var keyword = Ext.getCmp('invitationSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
									}
								}
							},{
								xtype: 'radiogroup',
								id:'ifOpen',
						        name:'ifOpen',
						        width:180,
						        columns : 2,
						        items: [
						                { boxLabel: '只看自己', name: 'rg',id:'rg1', inputValue: '0',checked:true,style:'margin-left:10px;margin-top:4px'},
						                { boxLabel: '查看所有', name: 'rg',id:'rg2', inputValue: '1',style:'margin-left:5px;margin-top:4px'}
						        ],
						        listeners :{
						        	change : function(r,newval,oldval){
						        		if(typeof(newval['rg'])=='string'){
						        			if(newval['rg']==1){
						        				Ext.getCmp('vendusername').show();
						        			}else{
						        				Ext.getCmp('vendusername').hide();
						        			}
							        		var grid = Ext.getCmp('invitationGrid');
											grid.store.load();
						        		}
						        		
						        	}
						        }
							}]
						},{
							xtype:'grid',
							id : 'invitationGrid',
							autoScroll:true,
							columnLines : true,
							region: 'center',
							store: invitationStore,
							columns:[Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25 ,
								align : 'center',
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex:'vendname',
							    align : 'center',
							    width : 300
							},{
							    text : '联系人',
							    dataIndex:'vendusername',
							    align : 'center'
							},{
							    text : '联系方式',
							    dataIndex:'vendusertel',
							    align : 'center'
							},{
								text : '邮箱',
								dataIndex : 'venduseremail',
								align : 'center'
							},{
							    text : '邀请时间',
							    dataIndex:'date',
							    align : 'center',
							    renderer : function(meta,value,record){
							    	return Ext.util.Format.date(new Date(parseInt(record.get('date'))),'Y-m-d');
							    }
							},{
								text : '邀请人',
								dataIndex:'vendusername',
								align :　'center',
								id : 'vendusername',
								hidden : true
							},{
								text : '邀请状态',
								width : 160,
								dataIndex : 'active',
								align : 'center',
								renderer : function(meta,value,record){
									var id = record.raw.id;
									var name = record.get('vendname');
							    	var contact = record.get('vendusername');
							    	var tel = record.get('vendusertel');
							    	var mail = record.get('venduseremail');
							    	var partnerInfo = {};
							    	partnerInfo.id = id ;
							    	partnerInfo.name = name ;
							    	partnerInfo.adminName = contact ;
							    	partnerInfo.adminTel = tel ;
							    	partnerInfo.adminEmail = mail ;
							    	var info = Ext.JSON.encode(partnerInfo);
									if(record.data.active==0){
										return "未注册/<input type='button' value='再次邀请' style='border:0;' onclick=invite('"+info+"','invitationGrid');>";
									}
									if(record.data.active==1){
										return "已注册";
									}
								}
							}/*,{
							    text : '次数',
							    dataIndex:'count',
							    align : 'center'
							}*/],
							bbar : Ext.create('Ext.PagingToolbar', {
								store : invitationStore,
								emptyMsg : "暂无数据"
							})
						}]
					},
					{
						title:'<font style="color:blue;">添加合作伙伴</font>',
						xtype : 'form',
						layout : 'border',
						id:'addNewPartnerTab',
						items:[{
							xtype : 'form',
							layout : 'column',
							region: 'north',
							height: 35,
							id:'addNewPartner',
							items:[{
								xtype : 'textfield',
								cls : 'test',
								id : 'enterpriseSearch',
								name:'enterpriseSearch',
								height : 30,
								emptyText : '输入企业名称等关键词进行搜索',
								width : 500
							},{
								xtype : 'button',
								text : "<p style='color:white'>搜索</p>",
								cls : 'btn-search',
								scale : 'medium',
							    listeners: {
							    	click:function(){
								    	var grid = Ext.getCmp('enterpriseListGrid');
								    	var keyword = Ext.getCmp('enterpriseSearch').value;
								    	Ext.apply(grid.getStore().proxy.extraParams, {
								    		keyword: keyword
										});
								    	var toolbar = grid.dockedItems.items[1];
								    	toolbar.moveFirst();
								    	//var text = "<div class='range' align='center'><span>暂未搜到与（"+keyword+"）相关的企业信息<a href='#' onclick='javascript:invite2(\""+keyword+"\")'><font color='red'>立即邀请注册</font></a>优软云<span></div>";
								    	var text = "<div class='range' align='center'><span>暂未搜到与（"+keyword+"）相关的企业信息<a href='#' onclick='javascript:inviteNew()'><font color='red'>立即邀请注册</font></a>优软云<span></div>";
										grid.getView().emptyText = text;
							    	}
							    }
							},{
								xtype : 'button',
								text : "<p style='color:white'>邀请注册</p>",
								cls : 'btn-newCompany',
								scale : 'medium',
							    listeners: {
							    	click: inviteNew
							    }
							}]
						},{
							xtype:'grid',
							id : 'enterpriseListGrid',
							store : enterpriseStore,
							columnLines : true,
							autoScroll:true,
							region: 'center',
							requires: [ 'erp.view.core.plugin.CopyPasteMenu'],
							plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
							columns : [Ext.create('Ext.grid.RowNumberer' , {
								text : '序号',
								width : 50 ,
								height :25,
								cls : 'x-grid-header-1',
								align : 'center'
							}),{
							    text : '企业名称',
							    dataIndex : 'name',
							    align : 'left',
							    width : 300
							},/*{
								text : '简称',
							    dataIndex : 'shortName',
							    align : 'left'
							},*/{
							    text : '营业执照',
							    dataIndex : 'businessCode',
							    align : 'left',
							    width : 250
							},{
							    text : '法定代表人',
							    dataIndex : 'corporation',
							    align : 'left',
							    width : 80
							},{
							    text : '企业UU',
							    dataIndex : 'uu',
							    align : 'left',
							    width : 120
							},{
								text : '联系人',
								dataIndex : 'adminName',
								align : 'center'
							},{
								text : '电话',
								dataIndex : 'adminTel',
								align : 'left'
							},{
								text : '邮箱',
								dataIndex : 'adminEmail',
								align : 'left'
							},{
								text : '行业',
								dataIndex : 'profession',
								align : 'left'
							},{
								text : '经营范围',
								dataIndex : 'tags',
								align : 'left'
							},{
								text : '企业地址',
								dataIndex : 'address',
								align : 'left',
								width : 220
							},{
								text : '状态',
								dataIndex : 'requestStatus',
								align : 'left',
								renderer : function(meta, val,record){
									var status = record.raw.status;
									if(status==313){
										return "优软云用户";
									}else{
										return "非优软云用户";
									}
								}
							},{
								text : 'method',
								width :0,
								dataIndex : 'method',
								align : 'left'
							},{
							    text : '操作/状态',	
							    align : 'center',
							    width : 160,
							    renderer: function(meta, val,record){
							    	var name = record.get('name');
							    	var contact = record.get('adminName');
							    	var tel = record.get('adminTel');
							    	var mail = record.get('adminEmail');
							    	var businessCode = record.get('businessCode');
							    	var requestStatus = record.get('requestStatus');
							    	var method = record.get('method');
							    	var address = record.get('address');
							    	var status = record.raw.status;
							    	var profession = record.get('profession');
							    	var tags = record.get('tags');
							    	var shortName = record.get('shortName');
							    	var uu = record.get('uu');
							    	var partnerInfo = {};
							    	partnerInfo.name = name ;
							    	partnerInfo.adminName = contact ;
							    	partnerInfo.adminTel = tel ;
							    	partnerInfo.adminEmail = mail ;
							    	partnerInfo.businessCode= businessCode;
							    	partnerInfo.address= address;
							    	partnerInfo.profession= profession;
							    	partnerInfo.tags= tags;
							    	partnerInfo.shortName= shortName;
							    	partnerInfo.uu= uu;
							    	var info = Ext.JSON.encode(partnerInfo);
							    	if(status==313){
							    		if(requestStatus == 311){
								    		if(method == 1){
								    			return "<span>已发出申请</span>";
								    		}else if(method ==0){
								    			return "<span>对方已申请</span>/<input type='button' value='立即查看'  onclick=watch2();>";
								    		}
								    	}else if (requestStatus == 313){
								    		//return "<span>已添加</span>/<input type='button' value='立即查看' onclick=watch('"+info+"');>";
								    		return "<span>已添加</span>/<a class='btn btn-primary' href='#' onclick=watch('"+info+"') type='button'>立即查看</a>";
								    	}else{
								    		//return "<input type='button' value='立即添加' onclick=newPartners('"+info+"','enterpriseListGrid');>";
								    		return "<a class='btn btn-primary' href='#' onclick=newPartners('"+info+"','enterpriseListGrid') type='button'>立即添加</a> <a class='btn btn-primary' href='#' onclick=addprevendor('"+info+"','enterpriseListGrid') type='button'>引进为供应商</a>";
								    	}
							    	}else{
							    		//return "<input type='button' value='立即邀请注册' onclick=invite('"+info+"','enterpriseListGrid');>";
							    		return "<a class='btn btn-primary' href='#' onclick=invite('"+info+"','enterpriseListGrid') type='button'>立即邀请注册</a>";
							    	}
							    	
							    }
							}],
							bbar : Ext.create('Ext.PagingToolbar', {
								id:'pagingbar',
								store : enterpriseStore,
								emptyMsg : "暂无数据"
							}),
							listeners: {//滚动条有时候没反应，添加此监听器
								scrollershow: function(scroller) {
									if (scroller && scroller.scrollEl) {
										scroller.clearManagedListeners();  
										scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
									}
								}
							}
						}]
					}]
				}]
			}]
		});	
		me.callParent(arguments);
	}
});
