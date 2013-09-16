package mixedserver.protocol.jsonrpc.filter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtFilter implements Filter  {

    private FilterConfig filterConfigObj = null;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        filterConfigObj = fc;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);

        PrintWriter out = response.getWriter();
        CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse)response);

        fc.doFilter(request, wrapper);

        try {
            JSONObject respObj = new JSONObject(wrapper.toString());
            if(respObj.has("error")) {
                respObj.put("success", false);
            } else {
                respObj.put("success", true);
            }

            out.write(respObj.toString());
        } catch(JSONException je) {
            out.write(wrapper.toString());
        }
    }

    @Override
    public void destroy() {
    }

}
