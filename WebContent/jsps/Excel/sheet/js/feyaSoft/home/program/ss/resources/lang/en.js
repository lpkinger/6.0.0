Ext.ns('feyaSoft.ss');

feyaSoft.ss.lang = {
	'updateLotOfFormula': 'This action will cause a lot of formula cells to update, please wait for a while',
	'fileIsNotSaved' : 'First is not save or backup yet.',
	'processing': '进行中',	
	'template' : '模板',	
	'newtemplate' : '新建模板',	
	'newfileFromTemp' : '从模板中新建文件',
	'openTemplate' : '打开模板',
	'saveAsTemplate' : '保存模板',
	'newSpreadsheet' : '新建',
	'greaterThan' : '大于',
	'lessThan': '少于',
	'isEqualTo': '等同于',
	'isNotEqualTo': '不等同于',
	'isBetween': '在..之间..',
	'isNotBetween': '不在..之间..',
	'setConditionFormat': '设置条件格式',
	'textContains': '文字包含',	
	'textNotContains': '文字不包含',
	'textExactly': '内容正确',
	'cellEmpty': '空单元格',
	'noConditionFormat': '当前账单不存在条件格式',
	'fixInvalid': '请先修复无效的条目',
	'text': '文本',
	'background': '背景',
	'condition': '条件',	
	'conditionFormat': '条件格式',
	'copySheet': '复制页簿',
	'loadSheet': '加载页簿',
    'insertFormula': '插入公式',
    'cancelFilter': '取消过滤',
    'hideRow' : '隐藏行',
    'hideCol' : '隐藏列',
    'unhideRow' : '不隐藏行',
    'unhideCol' : '不隐藏列',
    'formula' : '公式',
    'nameExisted': '名称已经存在',
    'likeReferenceRange': '它不能被评为一个范围在要么A1或R1C1语法',
    'reservedWord': '这个词是一个储备公式',
    'long250': '不能为空字符或超过250个字符',
    'letterNumber': '名称只能包含字母、数字和下划线且不能包含任何空格和以数字开头',
    'hint': '示意',
    'names': '名称',
	'nameRange': '输入一个名称的范围',	
	'nameManager': '管理名称范围',
	'markRange': '名称选择',
	'internetError': '服务器错误',
	'clearAll': '清空',
	'merge_cell': '合并',
	'cancel_merge': '取消合并',
    'can_not_change_combine': '不能改变合并后的单元格!',
    'paste': '粘贴',
    'moneyFormat':'金额格式',
    'percentFormat':'百分比格式',
    'commaFormat':'用逗号格式化',
    'moveLeftFormat': '小数位数递增',
    'moveRightFormat': '小数位数递减',
    'dateFormat':'日期格式',

	'canNotChangePartMergedCell':'执行操作需要选择包含整个合并的单元格',	
	'horizontally': '水平',
	'vertically': '垂直',
	'combineRanges': '合并范围',
	'deletePicture': '删除图片',
	'insertPicture': '插入图片',
	'removeFilter': '取消过滤',
	'expand_row_group': '展开行组',
	'collapse_row_group': '收缩行组',
	'expand_col_group': '展开列组',
	'collapse_col_group': '收缩列组',
    'change_dot_len' : '变化点数字号码',
    'cancelGroup': '取消分组',
    'dataRange': '数据范围:区域',
    'unfreezeGrid': '解除',
	'addGroup': '添加分组',
	'nameInvalid': '有含无效字符的名称,请查看有没有空格或任何一个包括“\,+ - * / %()(){ }”的字符',
	'nameDuplicated': '页簿的名称不能重复.',	
    'chartWizard': '图表向导',
    'bar': '条',
    'pie':'饼图',
    'bubble':'圆形',
    'xyscatter':'XY散开',
    'line':'线条',
    'area':'区域',
    'title':'标题',
    'subtitle':'副标题',
    'xAixs':'X轴',
    'yAixs':'Y轴',
    'zAixs':'Z轴',
    'displayLegend': '显示图例',
    'chartElement': '图示元素',
    'chartTitle': '改变标题、图例和表格设置',
    'displayGrid': '显示表格',
    'left':'左',
    'right':'右',
    'top':'上',
    'bottom':'下',
    'normal': '一般',
    'percentStacked': '堆放百分比',
    'stacked': '堆放',
    'exploded':'分解',
    'donut':'环状线圈',
    'explodedDonut': '分解线圈',
    'lineOnly': '仅线条',
    'pointOnly':'仅像素',
    'pointLine':'线条和像素',
    '3D':'3D',
    '3DLook':'3D 查看',
    'dataSeries':'数据序列',
    'dataRanges':'数据范围',
    'customizeDataForseries' : '定制数据范围为个人数据系列',
    'dataSeriesRows' :'行数据序列',
    'dataSeriesCols' :'列数据序列',
    'firstRowLabel':'第一行作为标签',
    'firstColumnLabel':'第一列作为标签',
    'chooseDataRange':'选择一个数据范围',
    'smoothlines':'平滑线',
    'chartType':'图表类型',
    'chooseChartType':'选择一个图表类型',
    'dataRange':'数据范围',
    'categories':'类别',
    'rangeforName':'范围名称',
    'useRightTimeFormat': '请用正确的时间格式，如：',
    'noHeadFoot2Show': '这个标签没有开头和结尾',
    'editHeadFoot': '编辑开头和结尾',
    'showHeadFoot': '显示开头和结尾',
    'hideHeadFoot': '隐藏开头和结尾',
    'header_footer': '开头和结尾',
    'notGridLine': '输出的时候不显示grid中的线条',       
    'printDesc': '<span style="padding: 0 0 0 10px; font: bold 14px Arial, sans-serif; ">打印设置</span><ul style="padding:8px 0 0 20px;"><li>其结果将是在新的浏览器选项卡打开PDF或HTML格式</li><li><b>Note</b>: 只有在这个电子表格将会处理.</li></ul>',
    'printSheet': '打印此表格',
    'noPermissionAction' : '您没有权限进行该操作或该文件被锁定',
	'saveChanges' : '保存修改',
	'saved' : '保存',
	'fail2Save' : '保存失败',
    'columnWidth': '列宽',
    'rowHeight': '行高',
    'clearSelection': '清除选择',
	'incell':'采用内嵌式 ',
	'paste_value_style':'粘贴值和样式',
	'only_paste_value':'粘贴',
	'only_paste_style':'粘贴样式',
	'only_paste_content':'粘贴内容',
	'transpose_paste':'转置粘贴',
	'findFilter':'查找并筛选',
	'filtering':'筛选',
	'createFilter':'创建过滤器',
	'wholeSheet':'整张表格',
	'row':'行',
	'column':'列',
	'for':'为',
	'changeFormat':'更改格式设置',
	'asc':'升序',
	'desc':'降序',
	'clearFilter':'清除过滤',
	'canNotSortMerge':'包含合并的单元格的内容不能排序',
	'selectAll':'选择所有',
	'no_data_in_selection':'在筛选之前请先选择单元格',
	'filters':'所有过滤',
	'filter_tip':'为选择创建自动过滤器',
	'asc_tip':'升序',
	'desc_tip':'降序',
	'copyCell':'复制单元格',
	'fillStyleContent':'填充内容和值',
	'fillSequence': '填充序列',
	'fillOnlyStyle':'仅填充样式',
	'fillOnlyContent':'填充不包括样式',
	'target':'目标',
	'all':'all',
	'with':'with',
	'at':'at',
	'in':'in',
	'replaceWith':'Replace With',
	'find':'Find',
	'replace':'Replace',
	'replaceNext':'Replace Next',
	'replaceAll':'Replace All',
	'findAll':'Find All',
	'findLast':'Find Last',
	'findNext':'Find Next',
    'formulaError':'公式错误',
	'bracketError':'在这个公式括号不匹配 ',
    'deleteRows': '删除整行',
    'deleteColumns': '删除列',
    'exportExcel2007': '导出Excel 2007 (.xlsx)',
    'exportExcel': '导出 MS Excel 2003 (.xls)',
    'exportCSV': '导出 CSV (.csv)',
    'exportPDF': '导出 PDF (.pdf)',
    'exportHTML': '导出 HTML (.html)',
    'freezeGrid': '冻结表格',
    'cancelFreeze': '取消冻结',
    'freeze_first_row': '冻结第一行',
    'freeze_first_col': '冻结第一列',
    'grid': '表格',
    'hideGridLine': '隐藏表格线',
    'showGridLine': '显示表格线',
    'insertRowAbove': '在上插入行',
    'insertRowBelow': '在下插入行',
    'insertColBefore': '在前插入列',
    'insertColAfter': '在后插入列',
    'inCellImage': '内嵌图片',
    'importExcel': '导入文件',
    'linkImage': '链接图像',
    'rowColumn': '行和列',
    'remove_format' : '移除格式',
    'selectImage': '选择图片',
    'splitGrid': '分割表格',
    'splitCell': '分割单元格',
    'combineCells': '合并单元格',
    'cancelSplit': '取消分割',
    'formatBrushAction': '格式刷',
    'commentAction': '评论',
    'freezeAction':'冻结grid',
    'unfreezeAction':'Cancel freeze grid',
    'undo':'取消',
    'redo':'重做',
    'no_action':'无操作',
    'step':'步',
    'steps':'步数',
    'regular':'规则',
    'number':'数值',
    'percent':'百分比',
    'money':'货币',
    'date':'日期',
    'time':'时间',
    'science':'科学计数',
    'text':'文本',
    'rmb':'人名币(中国)',
    'usdollar':'美元(美国)',
    'euro':'欧元(欧洲)',
    'pound':'英镑(英国)',
    'insertCellImage': '插入内嵌图片',
    'hyperlink':'超链接',
    'displayText':'显示文字',
    'webPage':'网页',
    'documentLocation':'文件位置',
    "insertFunction":"插入方法",
     //formula function type
    "spreadSheet":" 电子数据表",
    "lookup":"查找",
    "statistical":"Statistical",
    "string":"String",
    "logic":"Logical",
    "numeric":"Mathematical",
    "finicial":"Financial",
	"info":"Information",
    "cannotAutoFill":"一次最大自动填充行/列是50",
    'action_comment':function(cell){
        return '在此评论 '+cell;
    },
    'action_delete_comment':function(cell){
        return '删除评论 '+cell;
    },
    'action_clear':function(sc, ec){
        return '清除 '+sc+':'+ec;
    },
    'action_clear_format':function(sc, ec){
        return '清除格式 '+sc+':'+ec;
    },
    'action_input_cell':function(data, cell){
        return '输入 "'+data+'" 在 '+cell;
    },
    'action_freeze_change':function(cell){
        return '固定在 '+cell;
    },
    'action_row_resize':function(minx, maxx){
        if(minx == maxx){
            return '调整行 '+minx;
        }else{
            return '调整行 '+minx+':'+'行 '+maxx;
        }
    },
    'action_col_resize':function(miny, maxy){
        if(miny == maxy){
            return '调整列 '+miny;
        }else{
            return '调整列 '+miny+':'+'列 '+maxy;
        }
    },
    'action_row_hide':function(minx, maxx){
        if(minx == maxx){
            return '隐藏行 '+minx;
        }else{
            return '隐藏行 '+minx+':'+'行 '+maxx;
        }
    },
    'action_col_hide':function(miny, maxy){
        if(miny == maxy){
            return '隐藏列 '+miny;
        }else{
            return '隐藏列 '+miny+':'+'列 '+maxy;
        }
    },
    'action_row_show':function(minx, maxx){
        if(minx == maxx){
            return '显示行 '+minx;
        }else{
            return '显示行'+minx+':'+'行 '+maxx;
        }
    },
    'action_col_show':function(miny, maxy){
        if(miny == maxy){
            return '显示列 '+miny;
        }else{
            return '显示列 '+miny+':'+'列 '+maxy;
        }
    },
    'action_fit_image_cell':function(cell){
        return '更改 '+cell+' 去匹配内嵌的图片';
    },
    'action_split_grid':'分割表格',
    'action_fromat_brush':function(s, e){
        return '格式刷在'+s+':'+e;
    },
    'action_paste':'粘贴',
    'action_incell_image':function(cell){
        return '插入图片在 '+cell;
    },
    'action_apply_function':function(cell){
        return '计算在 '+cell;
    },
    'action_change_background':'更改背景',
    'action_change_attribute':'更改属性',
    'action_sort_asc':'Sort ascending',
    'action_sort_desc':'Sort descending',
    'action_border_all':'Border all',
    'action_border_out':'Border outside',
    'action_border_in':'Border inside',
    'action_border_none':'Border none',
    'action_border_left':'Border left',
    'action_border_right':'Border right',
    'action_border_top':'Border top',
    'action_border_bottom':'Border bottom',
    'action_insert_row_above':function(x){
        return 'Insert row above Row '+x;
    },
    'action_insert_row_below':function(x){
        return 'Insert row below Row '+x;
    },
    'action_insert_col_before':function(y){
        return 'Insert column before Column '+y;
    },
    'action_insert_col_after':function(y){
        return 'Insert column after Column '+y;
    },
    'action_delete_row':function(x){
        return 'Delete Row '+x;
    },
    'action_delete_col':function(y){
        return 'Delete Column '+y;
    },
    'action_hyperlink':function(cell){
        return 'Edit Hyperlink '+cell;
    },
    'action_delete_hyperlink':function(cell){
        return 'Remove Hyperlink '+cell;
    },
    
    
    'isi_add_formula' : 'Add/Edit Formula',
    'isi_company_lookup' : 'Company Lookup',
    'isi_import_company' : 'Import Companies'
};
