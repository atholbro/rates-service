package net.aholbrook.norm.jdbi.mapping

import net.aholbrook.norm.jdbi.result.JdbiRowData
import net.aholbrook.norm.jdbi.result.toRowData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class NormRowMapper : RowMapper<JdbiRowData> {
    override fun map(rs: ResultSet?, ctx: StatementContext?) = rs.toRowData()
}
