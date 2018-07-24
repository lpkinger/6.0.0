Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.CustomerDbfind', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	//BaseUtil: Ext.create('erp.util.BaseUtil'),
 	views:['core.form.Panel'],
	       init:function(){
	    	   var me = this;
	    	   this.control({
		    	   	'#search-Add':{//搜索框添加回车事件
			    	   	specialkey: function(field, e) {
							if (e.getKey() == Ext.EventObject.ENTER) {
								me.search();
							}
						}
					},
		    	   '#searchBtn':{//搜索按钮
		    	   		click:function(){
							me.search();
		    	   	}
		    	   },
	    		   'panel[id=GMap]':{
	    			   afterrender:function(c){
	    				   map = new BMap.Map("GMap"); // 创建Map实例
	    				   map.centerAndZoom("深圳", 13);
	    				   window.map = map;
	    				   map.enableScrollWheelZoom(true);
	    			   }
	    		   },
	    		   'panel[id=resultList]':{//收索结果
	    		   	 afterrender:function(g){
	    		   	 		var griddata=[];
	    		   			Ext.Ajax.request({
								url : basePath + 'common/getFieldsDatas.action',
								params: {
									caller: 'mobile_outaddress',
									fields: "md_company,md_address",
									condition: "md_company is not null and md_address is not null and rownum<11"
								},
								async:false,
								method : 'post',
								callback : function(opt, s, res){
									var r = new Ext.decode(res.responseText);
									if(r.exceptionInfo){
										showError(r.exceptionInfo);return;
									}
									if(r.success && r.data){
										data= new Ext.decode(r.data);
										Ext.each(data,function(d){
											d['SOURCE']='企业';
											griddata.push(d);
										});	
										Ext.getCmp('resultList').store.loadData(griddata);
									}
								}
							});
	      			 }
	  			 }
	  	})
	},
	search:function(){
	    var searchValue=Ext.getCmp('search-Add').value;
		var griddata=new Array();
		if(searchValue){
			var map= window.map ;
			Ext.Ajax.request({
				url : basePath + 'common/getFieldsDatas.action',
				params: {
					caller: 'mobile_outaddress',
					fields: "md_company,md_address",
					condition: "md_company like '%"+searchValue+"%' or md_company like '%"+searchValue+"%'"
				},
				async:false,
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);return;
					}
					if(r.success && r.data){
						data= new Ext.decode(r.data);
						Ext.each(data,function(d){
							d['SOURCE']='企业';
						griddata.push(d);
					});	
					var options = {      
						onSearchComplete: function(results){    
							if (local.getStatus() == BMAP_STATUS_SUCCESS){// 判断状态是否正确      
								for (var i = 0; i < results.getCurrentNumPois(); i ++){ 
									var dd= new Object();
									dd['MD_COMPANY']=results.getPoi(i).title;
									dd['MD_ADDRESS']=results.getPoi(i).address;
									dd['SOURCE']='地图';
									griddata.push(dd);
								} 
								Ext.getCmp('resultList').store.loadData(griddata);
							}else{
								Ext.getCmp('resultList').store.loadData(griddata);
							}    
							Ext.getCmp('remind').show();
						}      
					};      
					var local = new BMap.LocalSearch(map, options);      
					local.search(searchValue);
					}
			}
		});
	}
	}
});