package tkt.jobs;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PartitionJob {

	@Autowired
	DataSource dataSource;

	CallableStatement stmt = null;

	@Scheduled(fixedRate = 300000)
	public void reportCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		DateTime dt = new DateTime().minusMillis(300000);
		String datePartition = sdf.format(dt.toDate());

		/*
		 * ALTER TABLE genticket DROP PARTITION x; ALTER TABLE genticket
		 * PARTITION BY RANGE(UNIX_TIMESTAMP(created)) ( PARTITION x VALUES LESS
		 * THAN (UNIX_TIMESTAMP('2016-12-09 22:49:00')), PARTITION y VALUES LESS
		 * THAN MAXVALUE )
		 */

		String sql = "ALTER TABLE genticket PARTITION BY RANGE(UNIX_TIMESTAMP(created)) (PARTITION x VALUES LESS THAN (UNIX_TIMESTAMP('%s')), PARTITION y VALUES LESS THAN MAXVALUE)";
		sql = String.format(sql, datePartition);
		try {
			Connection conn = dataSource.getConnection();
			stmt = conn.prepareCall("ALTER TABLE genticket DROP PARTITION x");
			stmt.execute();

			stmt = conn.prepareCall(sql);
			stmt.execute();

			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
