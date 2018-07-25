<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<title>系统权限设置</title>
<style>
.x-grid-header{
	font-size: 12px;
	text-align: center;
}
.x-grid-search-trigger {
	cursor: pointer;
	background: url('<%=basePath %>resource/images/query.png') no-repeat center center !important;
}
.checked .x-form-checkbox {
	background-color: transparent;
	background-size: 16px;
    background-position: center ;
    background-image: url('<%=basePath %>resource/images/upgrade/bluegray/icon/maindetail/checked.png');
}
.x-toolbar .x-form-item-label{
	font-size:14px!important;
	line-height:20px!important;
}
#power_role .x-form-cb-label-after{
	font-size:14px;
}
.x-btn-default-toolbar-small-noicon button {
    height: 20px !important;
    line-height: 20px;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style type="text/css">
.x-column-header-inner {
	color:black;
    background-color: unset;
}
.x-column-header {
    background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #f9f9f9), color-stop(100%, #e3e4e6) );
    background-image: -webkit-linear-gradient(top, #f9f9f9, #e3e4e6);
    background-image: -moz-linear-gradient(top, #f9f9f9, #e3e4e6);
    background-image: -o-linear-gradient(top, #f9f9f9, #e3e4e6);
    background-image: -ms-linear-gradient(top, #f9f9f9, #e3e4e6);
    background-image: linear-gradient(top, #f9f9f9, #e3e4e6);
}
</style>
<script type="text/javascript">
var caller = "PositionPower";
var joborgnorelation = '<%=session.getAttribute("joborgnorelation")%>';
var em_type = '<%=session.getAttribute("em_type")%>';
var defaultPowerSetting = false; //系统参数设置是否有权限授权方式
var _role = false; //全局变量，判断“按角色授权”勾选框是否勾选
var defaultHrJobPowerExists = false; //全局变量，判断默认分配岗位下属权限勾选框是否勾选
//override local query
Ext.override(Ext.form.field.ComboBox, {
	doQuery: function(queryString, forceAll, rawQuery) {
        queryString = queryString || '';

        // store in object and pass by reference in 'beforequery'
        // so that client code can modify values.
        var me = this,
            qe = {
                query: queryString,
                forceAll: forceAll,
                combo: me,
                cancel: false
            },
            store = me.store,
            isLocalMode = me.queryMode === 'local';

        if (me.fireEvent('beforequery', qe) === false || qe.cancel) {
            return false;
        }

        // get back out possibly modified values
        queryString = qe.query;
        forceAll = qe.forceAll;

        // query permitted to run
        if (forceAll || (queryString.length >= me.minChars)) {
            // expand before starting query so LoadMask can position itself correctly
            me.expand();

            // make sure they aren't querying the same thing
            if (!me.queryCaching || me.lastQuery !== queryString) {
                me.lastQuery = queryString;

                if (isLocalMode) {
                    // forceAll means no filtering - show whole dataset.
                    if (forceAll) {
                        store.clearFilter();
                    } else {
                        // Clear filter, but supress event so that the BoundList is not immediately updated.
                        store.clearFilter(true);
                        store.filter([new Ext.util.Filter({
                            id: me.id + '-query-filter',
                            anyMatch: me.anyMatch,
                            caseSensitive: me.caseSensitive,
                            root: 'data',
                            property: me.displayField,
                            value: queryString
                        })]);
                    }
                } else {
                    // Set flag for onLoad handling to know how the Store was loaded
                    me.rawQuery = rawQuery;

                    // In queryMode: 'remote', we assume Store filters are added by the developer as remote filters,
                    // and these are automatically passed as params with every load call, so we do *not* call clearFilter.
                    if (me.pageSize) {
                        // if we're paging, we've changed the query so start at page 1.
                        me.loadPage(1);
                    } else {
                        store.load({
                            params: me.getParams(queryString)
                        });
                    }
                }
            }

            // Clear current selection if it does not match the current value in the field
            if (me.getRawValue() !== me.getDisplayValue()) {
                me.ignoreSelection++;
                me.picker.getSelectionModel().deselectAll();
                me.ignoreSelection--;
            }

            if (isLocalMode) {
                me.doAutoSelect();
            }
            if (me.typeAhead) {
                me.doTypeAhead();
            }
        }
        return true;
    }
});
var app;
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载

//获取默认授权方式
Ext.Ajax.request({
	url:basePath + 'common/getFieldData.action',
	async:false,
	params:{
		caller:'configs',
		field:'data',
		condition:"code='defaultPower' and caller='sys'"
	},
	callback : function(options,success,response){
		var res = new Ext.decode(response.responseText);
		if(res.success){
			if(res.data){
				defaultPowerSetting = true;
			}
			if(res.data&&res.data==1){
				_role = true;
			}
		}
	}
});

//获取是否设置默认岗位下属权限
Ext.Ajax.request({
	url:basePath + 'common/getFieldData.action',
	async:false,
	params:{
		caller:'configs',
		field:'data',
		condition:"code='defaultHrJobPowerExists' and caller='sys'"
	},
	callback : function(options,success,response){
		var res = new Ext.decode(response.responseText);
		if(res.success){
			if(res.data&&res.data==1){
				defaultHrJobPowerExists = true;
			}
		}
	}
});

Ext.Ajax.request({//拿到treegrid数据
	url : basePath + 'common/singleGridPanel.action',
	params: {
		caller: caller, 
		condition: ''
	},
	callback : function(options,success,response){
		var res = new Ext.decode(response.responseText);
		if(res.columns){
        	fields = res.fields;
        	columns = new Array();
        	var keys = ['locked', 'summaryType', 'logic', 'renderer'];
        	Ext.each(res.columns, function(c){
        		var o = new Object();
        		var key = Ext.Object.getKeys(c);
        		Ext.each(key, function(k){
        			if(!Ext.Array.contains(keys, k)){
        				o[k] = c[k];
        			}
        		});
        		columns.push(o);
        	});
        	app =  new Ext.app.Application({
			//var app = Ext.application({
			    name: 'erp',//为应用程序起一个名字,相当于命名空间
			    appFolder: basePath+'app',//app文件夹所在路径
			    controllers: [//声明所用到的控制层
			        'ma.Power'
			    ],
			    launch: function() {
			    	Ext.create('erp.view.ma.Power');//创建视图
			    }
			});
		} else if(res.exceptionInfo){
			Ext.Msg.alert('提示',res.exceptionInfo,function(){
				window.close();
			});
		}
	}
});
function Delete(){//特殊权限删除
	var cal = arguments[0];
	var self = arguments[1];
	var joid = arguments[2];
	var grid=Ext.getCmp('special'+cal);
	if(typeof(self)!='undefined'){
		grid=Ext.getCmp('sceneBtn'+cal);
	}
    var lastselected=grid.getSelectionModel().getLastSelected();
    var id=lastselected.data.ssp_id;
    var sb_id = lastselected.data.sb_id;
    if(id==0&&!sb_id){
    	grid.getStore().remove(lastselected);
    }else{
    	var caller=lastselected.data.ssp_caller;
        warnMsg($I18N.common.msg.ask_del_main, function(btn){
        	if(btn == 'yes'){
        		grid.setLoading(true);
    			Ext.Ajax.request({
    				url : basePath +'ma/power/deleteSysSpecialPowerById.action',
    				params: {
    					id: id,
    					caller:caller,
    					sbid:sb_id
    				},
    				method : 'post',
    				callback : function(options,success,response){
    					grid.setLoading(false);
    					var rs = new Ext.decode(response.responseText);
    					if(rs.success){
    						if(!sb_id){
    							grid.getStore().remove(lastselected);
    						}else{
    							if(joid){
    								Ext.getCmp('grid').getSceneBtnPowers(cal, grid,joid,self);
    							}else{
    								Ext.getCmp('grid').getSceneBtnPowers(cal, grid);
    							}
    						}
    					} else {
    						delFailure();
    					}
    				}
    			});
            }
        }); 
    }
};
</script>
</head>
<body >
</body>
</html>