<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String lang=request.getLocale().getLanguage().toString();
if(null!=request.getSession().getAttribute("WW_TRANS_I18N_LOCALE")){
	lang=request.getSession().getAttribute("WW_TRANS_I18N_LOCALE").toString().split("_")[0];
}
%>

<html>
	<head>
	<script type="text/javascript" src="config.js"></script>
	<script>
		if(parent==window)
			location.href = '';
		var $d, $dp, $pdp = parent.$dp, $dt, $tdt, $sdt, $IE=$pdp.ie, $FF = $pdp.ff,$OPERA=$pdp.opera, $ny, $cMark = false;
		if ($pdp.eCont) {
			$dp = {};
			for (var p in $pdp) {
				$dp[p] = $pdp[p];
			}
		}
		else
			$dp = $pdp;
			
		$dp.getLangIndex = function(name){
			var arr = langList;
			for (var i = 0; i < arr.length; i++) {
				if (arr[i].name == name) {
					return i;
				}
			}
			return -1;
		}
		
		$dp.getLang = function(name){
			var index = $dp.getLangIndex(name);
			if (index == -1) {
				index = 0;
			}
			return langList[index];
		}
		$dp.realLang = $dp.getLang($dp.lang);
		var currentLocale='<%=lang%>';
		if("zh"==currentLocale){
			$dp.realLang.name="zh-cn";
		}else if("en"==currentLocale){
			$dp.realLang.name="en";
		}
		document.write("<script src='lang/" + $dp.realLang.name + ".js' charset='" + $dp.realLang.charset + "'><\/script>");
		
		for (var i = 0; i < skinList.length; i++) {
		    document.write('<link rel="stylesheet" type="text/css" href="skin/' + skinList[i].name + '/datepicker.css" title="' + skinList[i].name + '" charset="' + skinList[i].charset + '" disabled="true"/>');
		}
	
	</script>
	<script type="text/javascript" src="calendar.js"></script>
	</head>
	<body leftmargin="0" topmargin="0" onload="$c.autoSize()" tabindex=0></body>
</html>
<script>new My97DP();</script>